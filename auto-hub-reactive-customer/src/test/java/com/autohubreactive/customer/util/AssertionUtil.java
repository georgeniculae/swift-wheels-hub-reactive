package com.autohubreactive.customer.util;

import com.autohubreactive.dto.RegisterRequest;
import com.autohubreactive.dto.RegistrationResponse;
import com.autohubreactive.dto.UserInfo;
import com.autohubreactive.dto.UserUpdateRequest;
import org.keycloak.representations.idm.UserRepresentation;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtil {

    public static void assertRegistrationResponse(UserRepresentation userRepresentation, RegistrationResponse registrationResponse) {
        assertEquals(userRepresentation.getUsername(), registrationResponse.username());
        assertEquals(userRepresentation.getFirstName(), registrationResponse.firstName());
        assertEquals(userRepresentation.getLastName(), registrationResponse.lastName());
        assertEquals(userRepresentation.getEmail(), registrationResponse.email());
        assertEquals(userRepresentation.getAttributes().get("address").getFirst(), registrationResponse.address());
        assertEquals(userRepresentation.getAttributes().get("dateOfBirth").getFirst(), registrationResponse.dateOfBirth().toString());
    }

    public static void assertRegistrationResponse(RegisterRequest registerRequest, RegistrationResponse registrationResponse) {
        assertEquals(registerRequest.username(), registrationResponse.username());
        assertEquals(registerRequest.firstName(), registrationResponse.firstName());
        assertEquals(registerRequest.lastName(), registrationResponse.lastName());
        assertEquals(registerRequest.email(), registrationResponse.email());
        assertEquals(registerRequest.address(), registrationResponse.address());
        assertEquals(registerRequest.dateOfBirth(), registrationResponse.dateOfBirth());
    }

    public static void assertUserRepresentation(UserUpdateRequest userUpdateRequest, UserRepresentation userRepresentation) {
        assertEquals(userUpdateRequest.username(), userRepresentation.getUsername());
        assertEquals(userUpdateRequest.firstName(), userRepresentation.getFirstName());
        assertEquals(userUpdateRequest.lastName(), userRepresentation.getLastName());
        assertEquals(userUpdateRequest.email(), userRepresentation.getEmail());
    }

    public static void assertUserDetails(UserRepresentation userRepresentation, UserInfo userInfo) {
        assertEquals(userRepresentation.getUsername(), userInfo.username());
        assertEquals(userRepresentation.getFirstName(), userInfo.firstName());
        assertEquals(userRepresentation.getLastName(), userInfo.lastName());
        assertEquals(userRepresentation.getEmail(), userInfo.email());
        assertEquals(userRepresentation.getAttributes().get("address").getFirst(), userInfo.address());
        assertEquals(userRepresentation.getAttributes().get("dateOfBirth").getFirst(), userInfo.dateOfBirth().toString());
    }

    public static void assertUserDetails(UserUpdateRequest userUpdateRequest, UserInfo userInfo) {
        assertEquals(userUpdateRequest.username(), userInfo.username());
        assertEquals(userUpdateRequest.firstName(), userInfo.firstName());
        assertEquals(userUpdateRequest.lastName(), userInfo.lastName());
        assertEquals(userUpdateRequest.email(), userInfo.email());
        assertEquals(userUpdateRequest.address(), userInfo.address());
        assertEquals(userUpdateRequest.dateOfBirth(), userInfo.dateOfBirth());
    }

}
