package com.swiftwheelshub.customer.util;

import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

public class TestData {

    public static UserRepresentation getUserRepresentation() {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue("password");

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId("1");
        userRepresentation.setUsername("user");
        userRepresentation.setFirstName("Ion");
        userRepresentation.setLastName("Popescu");
        userRepresentation.setEmail("ionpopescu@email.com");
        userRepresentation.setCredentials(List.of(passwordCredentials));
        userRepresentation.singleAttribute("address", "Ploiesti");
        userRepresentation.singleAttribute("dateOfBirth", "1980-03-05");

        return userRepresentation;
    }

}
