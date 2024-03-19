package com.swiftwheelshubreactive.customer.service;

import com.swiftwheelshubreactive.customer.mapper.UserMapper;
import com.swiftwheelshubreactive.dto.RegisterRequest;
import com.swiftwheelshubreactive.dto.RegistrationResponse;
import com.swiftwheelshubreactive.dto.UserInfo;
import com.swiftwheelshubreactive.dto.UserUpdateRequest;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubResponseStatusException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KeycloakUserService {

    private static final String ADDRESS = "address";

    private static final String DATE_OF_BIRTH = "dateOfBirth";

    private static final String USER = "user";

    private static final String OFFLINE_ACCESS = "offline_access";

    private static final String OPENING_BRACE = "{";

    private static final String CLOSE_BRACE = "}";

    private static final String ROLE = "role_";

    private static final String $ = "$";

    @Value($ + OPENING_BRACE + "keycloak.realm}")
    private String realm;

    private final Keycloak keycloak;

    private final UserMapper userMapper;

    public UserInfo findUserByUsername(String username) {
        UserRepresentation userRepresentation = getUserRepresentation(username);

        return userMapper.mapUserToUserDetails(userRepresentation);
    }

    public UserInfo getCurrentUser(String username) {
        return findUserByUsername(username);
    }

    public Integer countUsers() {
        return getUsersResource().count();
    }

    public RegistrationResponse registerCustomer(RegisterRequest request) {
        validateRequest(request);
        UserRepresentation userRepresentation = createUserRepresentation(request);

        try (Response response = getUsersResource().create(userRepresentation)) {
            final int statusCode = response.getStatus();

            if (HttpStatus.CREATED.value() == statusCode) {
                return getRegistrationResponse(userRepresentation, response, request);
            }

            throw new SwiftWheelsHubResponseStatusException(
                    HttpStatusCode.valueOf(statusCode),
                    "User could not be created: " + response.getStatusInfo().getReasonPhrase()
            );
        }
    }

    public UserInfo updateUser(String id, UserUpdateRequest userUpdateRequest) {
        UserResource userResource = findById(id);

        UserRepresentation userRepresentation = userMapper.mapToUserRepresentation(userUpdateRequest);
        userRepresentation.singleAttribute(ADDRESS, userUpdateRequest.address());
        userRepresentation.singleAttribute(DATE_OF_BIRTH, userUpdateRequest.dateOfBirth().toString());

        try {
            userResource.update(userRepresentation);
        } catch (Exception e) {
            handleRestEasyCall(e);
        }

        return userMapper.mapUserToUserDetails(userRepresentation);
    }

    public void deleteUserByUsername(String username) {
        UserRepresentation userRepresentation = getUserRepresentation(username);
        UserResource userResource = findById(userRepresentation.getId());

        try {
            userResource.remove();
        } catch (Exception e) {
            handleRestEasyCall(e);
        }
    }

    public void signOut(String id) {
        UserRepresentation userRepresentation = getUserRepresentation(id);

        try {
            findById(userRepresentation.getId()).logout();
        } catch (Exception e) {
            handleRestEasyCall(e);
        }
    }

    private UsersResource getUsersResource() {
        return getRealmResource().users();
    }

    private UserResource findById(String id) {
        return getUsersResource().get(id);
    }

    private CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);

        return passwordCredentials;
    }

    private UserRepresentation createUserRepresentation(RegisterRequest request) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(request.username());
        userRepresentation.setFirstName(request.firstName());
        userRepresentation.setLastName(request.lastName());
        userRepresentation.setEmail(request.email());
        userRepresentation.setCredentials(List.of(createPasswordCredentials(request.password())));
        userRepresentation.singleAttribute(ADDRESS, request.address());
        userRepresentation.singleAttribute(DATE_OF_BIRTH, request.dateOfBirth().toString());
        userRepresentation.setEmailVerified(!request.needsEmailVerification());
        userRepresentation.setEnabled(true);

        return userRepresentation;
    }

    private void verifyEmail(String userId) {
        try {
            findById(userId).sendVerifyEmail();
        } catch (Exception e) {
            handleRestEasyCall(e);
        }
    }

    private RegistrationResponse getRegistrationResponse(UserRepresentation userRepresentation, Response response,
                                                         RegisterRequest request) {
        String createdId = CreatedResponseUtil.getCreatedId(response);
        UserResource userResource = findById(createdId);
        handleUserRole();

        try {
            userResource.resetPassword(createPasswordCredentials(request.password()));
            RoleRepresentation roleRepresentation = getUserRoleRepresentation();
            userResource.roles().realmLevel().add(List.of(roleRepresentation));
        } catch (Exception e) {
            handleRestEasyCall(e);
        }

        if (request.needsEmailVerification()) {
            verifyEmail(getUserId(userRepresentation.getUsername()));
        }

        return userMapper.mapToRegistrationResponse(userRepresentation);
    }

    private UserRepresentation getUserRepresentation(String username) {
        List<UserRepresentation> userRepresentations = getUserRepresentations(username);

        if (userRepresentations.isEmpty()) {
            throw new SwiftWheelsHubNotFoundException("User with username " + username + " doesn't exist");
        }

        return userRepresentations.getFirst();
    }

    private List<UserRepresentation> getUserRepresentations(String username) {
        return getUsersResource().searchByUsername(username, true);
    }

    private String getUserId(String username) {
        return getUserRepresentation(username).getId();
    }

    private void handleUserRole() {
        boolean isRoleNonexistent = getRealmResource().roles()
                .list()
                .stream()
                .map(RoleRepresentation::getName)
                .noneMatch(USER::equals);

        if (isRoleNonexistent) {
            RoleRepresentation roleRepresentation = new RoleRepresentation();
            roleRepresentation.setName(USER);
            roleRepresentation.setDescription($ + OPENING_BRACE + ROLE + USER + CLOSE_BRACE);

            RolesResource rolesResource = getRolesResource();
            rolesResource.create(roleRepresentation);

            getRoleResource().addComposites(List.of(rolesResource.get(OFFLINE_ACCESS).toRepresentation()));
        }
    }

    private RoleRepresentation getUserRoleRepresentation() {
        return getRoleResource().toRepresentation();
    }

    private RoleResource getRoleResource() {
        return getRolesResource().get(USER);
    }

    private RolesResource getRolesResource() {
        return getRealmResource().roles();
    }

    private RealmResource getRealmResource() {
        return keycloak.realm(realm);
    }

    private void validateRequest(RegisterRequest request) {
        if (request.password().length() < 8) {
            throw new SwiftWheelsHubResponseStatusException(HttpStatus.BAD_REQUEST, "Password too short");
        }

        if (Period.between(request.dateOfBirth(), LocalDate.now()).getYears() < 18) {
            throw new SwiftWheelsHubResponseStatusException(HttpStatus.BAD_REQUEST, "Customer is under 18 years old");
        }
    }

    private void handleRestEasyCall(Exception e) {
        if (e instanceof NotFoundException) {
            throw new SwiftWheelsHubNotFoundException("User not found");
        }

        throw new SwiftWheelsHubException(e);
    }

}
