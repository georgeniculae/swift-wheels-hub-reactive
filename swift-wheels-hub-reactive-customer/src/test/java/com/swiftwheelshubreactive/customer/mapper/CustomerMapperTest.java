package com.swiftwheelshubreactive.customer.mapper;

import com.swiftwheelshubreactive.customer.util.AssertionUtils;
import com.swiftwheelshubreactive.customer.util.TestData;
import com.swiftwheelshubreactive.customer.util.TestUtils;
import com.swiftwheelshubreactive.dto.RegistrationResponse;
import com.swiftwheelshubreactive.dto.UserInfo;
import com.swiftwheelshubreactive.dto.UserUpdateRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class CustomerMapperTest {

    private final CustomerMapper customerMapper = new CustomerMapperImpl();

    @Test
    void mapToUserRepresentationTest_success() {
        UserUpdateRequest userUpdateRequest =
                TestUtils.getResourceAsJson("/data/UserUpdateRequest.json", UserUpdateRequest.class);

        UserRepresentation userRepresentation = customerMapper.mapToUserRepresentation(userUpdateRequest);

        AssertionUtils.assertUserRepresentation(userUpdateRequest, userRepresentation);
    }

    @Test
    void mapToUserRepresentationTest_null() {
        assertNull(customerMapper.mapToUserRepresentation(null));
    }

    @Test
    void mapUserToUserDetailsTest_success() {
        UserRepresentation userRepresentation = TestData.getUserRepresentation();

        UserInfo userInfo = customerMapper.mapUserToUserDetails(userRepresentation);

        AssertionUtils.assertUserDetails(userRepresentation, userInfo);
    }

    @Test
    void mapUserToUserDetailsTest_null() {
        assertNull(customerMapper.mapUserToUserDetails(null));
    }

    @Test
    void mapToRegistrationResponseTest_success() {
        UserRepresentation userRepresentation = TestData.getUserRepresentation();

        RegistrationResponse registrationResponse = customerMapper.mapToRegistrationResponse(userRepresentation);

        AssertionUtils.assertRegistrationResponse(userRepresentation, registrationResponse);
    }

    @Test
    void mapToRegistrationResponseTest_null() {
        assertNull(customerMapper.mapToRegistrationResponse(null));
    }

}
