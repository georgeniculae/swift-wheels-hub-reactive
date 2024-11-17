package com.swiftwheelshubreactive.customer.service;

import com.swiftwheelshubreactive.customer.mapper.CustomerMapper;
import com.swiftwheelshubreactive.customer.mapper.CustomerMapperImpl;
import com.swiftwheelshubreactive.customer.util.AssertionUtil;
import com.swiftwheelshubreactive.customer.util.TestData;
import com.swiftwheelshubreactive.customer.util.TestUtil;
import com.swiftwheelshubreactive.dto.RegisterRequest;
import com.swiftwheelshubreactive.dto.RegistrationResponse;
import com.swiftwheelshubreactive.dto.UserInfo;
import com.swiftwheelshubreactive.dto.UserUpdateRequest;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubResponseStatusException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ServerResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KeycloakUserServiceTest {

    @InjectMocks
    private KeycloakUserService keycloakUserService;

    @Mock
    private Keycloak keycloak;

    @Mock
    private UsersResource usersResource;

    @Mock
    private UserResource userResource;

    @Mock
    private RealmResource realmResource;

    @Mock
    private RolesResource rolesResource;

    @Mock
    private RoleResource roleResource;

    @Mock
    private RoleRepresentation roleRepresentation;

    @Mock
    private RoleMappingResource roleMappingResource;

    @Mock
    private RoleScopeResource roleScopeResource;

    @Spy
    private CustomerMapper customerMapper = new CustomerMapperImpl();

    @Test
    void findAllUsersTest_success() {
        ReflectionTestUtils.setField(keycloakUserService, "realm", "realm");

        UserRepresentation userRepresentation = TestData.getUserRepresentation();

        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.list()).thenReturn(List.of(userRepresentation));

        List<UserInfo> allCustomers = keycloakUserService.findAllUsers();

        assertFalse(allCustomers.isEmpty());
    }

    @Test
    void findUserByUsernameTest_success() {
        ReflectionTestUtils.setField(keycloakUserService, "realm", "realm");

        UserRepresentation userRepresentation = TestData.getUserRepresentation();

        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.searchByUsername(anyString(), anyBoolean())).thenReturn(List.of(userRepresentation));

        UserInfo user = keycloakUserService.findUserByUsername("user");

        AssertionUtil.assertUserDetails(userRepresentation, user);
    }

    @Test
    void findUserByUsernameTest_noUserFound() {
        ReflectionTestUtils.setField(keycloakUserService, "realm", "realm");

        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.searchByUsername(anyString(), anyBoolean())).thenReturn(List.of());

        SwiftWheelsHubNotFoundException notFoundException =
                assertThrows(SwiftWheelsHubNotFoundException.class, () -> keycloakUserService.findUserByUsername("user"));

        assertNotNull(notFoundException);
    }

    @Test
    void getCurrentUserTest_success() {
        ReflectionTestUtils.setField(keycloakUserService, "realm", "realm");

        UserRepresentation userRepresentation = TestData.getUserRepresentation();

        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.searchByUsername(anyString(), anyBoolean())).thenReturn(List.of(userRepresentation));

        UserInfo currentUser = keycloakUserService.getCurrentUser("user");

        AssertionUtil.assertUserDetails(userRepresentation, currentUser);

        verify(customerMapper).mapUserToUserInfo(any(UserRepresentation.class));
    }

    @Test
    void getCurrentUserTest_errorOnFindingByUsername() {
        ReflectionTestUtils.setField(keycloakUserService, "realm", "realm");

        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.searchByUsername(anyString(), anyBoolean())).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> keycloakUserService.findUserByUsername("user"));
    }

    @Test
    void getCurrentUserTest_noUsersFound() {
        ReflectionTestUtils.setField(keycloakUserService, "realm", "realm");

        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.searchByUsername(anyString(), anyBoolean())).thenReturn(List.of());

        SwiftWheelsHubNotFoundException notFoundException =
                assertThrows(SwiftWheelsHubNotFoundException.class, () -> keycloakUserService.getCurrentUser("user"));

        assertNotNull(notFoundException);
    }

    @Test
    void registerCustomerTest_success() {
        ReflectionTestUtils.setField(keycloakUserService, "realm", "realm");

        RegisterRequest registerRequest =
                TestUtil.getResourceAsJson("/data/RegisterRequest.json", RegisterRequest.class);

        Headers<Object> headers = new Headers<>();
        headers.put("test", List.of());
        Response response = new ServerResponse(null, 201, headers);

        try (var _ = mockStatic(CreatedResponseUtil.class)) {
            when(CreatedResponseUtil.getCreatedId(any())).thenReturn("id");
            when(keycloak.realm(anyString())).thenReturn(realmResource);
            when(realmResource.roles()).thenReturn(rolesResource);
            when(rolesResource.list()).thenReturn(List.of(roleRepresentation));
            when(rolesResource.get(anyString())).thenReturn(roleResource);
            when(realmResource.users()).thenReturn(usersResource);
            when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
            when(usersResource.get(anyString())).thenReturn(userResource);
            when(rolesResource.get(anyString())).thenReturn(roleResource);
            doNothing().when(roleResource).addComposites(anyList());
            when(roleResource.toRepresentation()).thenReturn(roleRepresentation);
            doNothing().when(userResource).resetPassword(any(CredentialRepresentation.class));
            when(userResource.roles()).thenReturn(roleMappingResource);
            when(roleMappingResource.realmLevel()).thenReturn(roleScopeResource);
            doNothing().when(roleScopeResource).add(anyList());

            RegistrationResponse registrationResponse = keycloakUserService.registerCustomer(registerRequest);

            AssertionUtil.assertRegistrationResponse(registerRequest, registrationResponse);

            verify(customerMapper).mapToRegistrationResponse(any(UserRepresentation.class));
        }
    }

    @Test
    void registerCustomerTest_customerUnderAge() {
        RegisterRequest registerRequest =
                TestUtil.getResourceAsJson("/data/RegisterRequestAgeBelow18.json", RegisterRequest.class);

        SwiftWheelsHubResponseStatusException swiftWheelsHubResponseStatusException =
                assertThrows(SwiftWheelsHubResponseStatusException.class, () -> keycloakUserService.registerCustomer(registerRequest));

        assertNotNull(swiftWheelsHubResponseStatusException);
        assertThat(swiftWheelsHubResponseStatusException.getMessage()).contains("Customer is under 18 years old");
    }

    @Test
    void registerCustomerTest_passwordTooShort() {
        RegisterRequest registerRequest =
                TestUtil.getResourceAsJson("/data/RegisterRequestPasswordTooShort.json", RegisterRequest.class);

        SwiftWheelsHubResponseStatusException swiftWheelsHubResponseStatusException =
                assertThrows(SwiftWheelsHubResponseStatusException.class, () -> keycloakUserService.registerCustomer(registerRequest));

        assertNotNull(swiftWheelsHubResponseStatusException);
        assertThat(swiftWheelsHubResponseStatusException.getMessage()).contains("Password too short");
    }

    @Test
    void updateUserTest_success() {
        ReflectionTestUtils.setField(keycloakUserService, "realm", "realm");

        UserUpdateRequest userUpdateRequest =
                TestUtil.getResourceAsJson("/data/UserUpdateRepresentation.json", UserUpdateRequest.class);

        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(anyString())).thenReturn(userResource);
        doNothing().when(userResource).update(any(UserRepresentation.class));

        UserInfo userInfo = keycloakUserService.updateUser("user", userUpdateRequest);

        AssertionUtil.assertUserDetails(userUpdateRequest, userInfo);
    }

    @Test
    void updateUserTest_errorOnFindingById() {
        ReflectionTestUtils.setField(keycloakUserService, "realm", "realm");

        UserUpdateRequest userUpdateRequest =
                TestUtil.getResourceAsJson("/data/UserUpdateRepresentation.json", UserUpdateRequest.class);

        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(anyString())).thenReturn(userResource);
        doThrow(new NotFoundException()).when(userResource).update(any(UserRepresentation.class));

        SwiftWheelsHubNotFoundException notFoundException =
                assertThrows(SwiftWheelsHubNotFoundException.class, () -> keycloakUserService.updateUser("user", userUpdateRequest));

        assertNotNull(notFoundException);
    }

    @Test
    void deleteUserByUsernameTest_success() {
        ReflectionTestUtils.setField(keycloakUserService, "realm", "realm");

        UserRepresentation userRepresentation = TestData.getUserRepresentation();

        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(anyString())).thenReturn(userResource);
        when(usersResource.searchByUsername(anyString(), anyBoolean())).thenReturn(List.of(userRepresentation));
        doNothing().when(userResource).remove();

        assertDoesNotThrow(() -> keycloakUserService.deleteUserByUsername("user"));
    }

    @Test
    void deleteUserByUsernameTest_userNotFound() {
        ReflectionTestUtils.setField(keycloakUserService, "realm", "realm");

        UserRepresentation userRepresentation = TestData.getUserRepresentation();

        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(anyString())).thenReturn(userResource);
        when(usersResource.searchByUsername(anyString(), anyBoolean())).thenReturn(List.of(userRepresentation));
        doThrow(new NotFoundException()).when(userResource).remove();

        assertThrows(SwiftWheelsHubNotFoundException.class, () -> keycloakUserService.deleteUserByUsername("user"));
    }

    @Test
    void logoutTest_success() {
        ReflectionTestUtils.setField(keycloakUserService, "realm", "realm");

        UserRepresentation userRepresentation = TestData.getUserRepresentation();

        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.searchByUsername(anyString(), anyBoolean())).thenReturn(List.of(userRepresentation));
        when(usersResource.get(anyString())).thenReturn(userResource);
        doNothing().when(userResource).logout();

        assertDoesNotThrow(() -> keycloakUserService.signOut("user"));
    }

    @Test
    void logoutTest_errorOnLogout() {
        ReflectionTestUtils.setField(keycloakUserService, "realm", "realm");

        UserRepresentation userRepresentation = TestData.getUserRepresentation();

        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.searchByUsername(anyString(), anyBoolean())).thenReturn(List.of(userRepresentation));
        when(usersResource.get(anyString())).thenReturn(userResource);
        doThrow(new NotFoundException()).when(userResource).logout();

        SwiftWheelsHubNotFoundException notFoundException =
                assertThrows(SwiftWheelsHubNotFoundException.class, () -> keycloakUserService.signOut("user"));

        assertNotNull(notFoundException);
    }

}
