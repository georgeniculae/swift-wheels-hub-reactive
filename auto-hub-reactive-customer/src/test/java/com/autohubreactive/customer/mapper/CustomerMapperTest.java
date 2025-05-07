package com.autohubreactive.customer.mapper;

import com.autohubreactive.customer.util.AssertionUtil;
import com.autohubreactive.customer.util.TestData;
import com.autohubreactive.customer.util.TestUtil;
import com.autohubreactive.dto.customer.RegistrationResponse;
import com.autohubreactive.dto.customer.UserInfo;
import com.autohubreactive.dto.customer.UserUpdateRequest;
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
                TestUtil.getResourceAsJson("/data/UserUpdateRequest.json", UserUpdateRequest.class);

        UserRepresentation userRepresentation = customerMapper.mapToUserRepresentation(userUpdateRequest);

        AssertionUtil.assertUserRepresentation(userUpdateRequest, userRepresentation);
    }

    @Test
    void mapToUserRepresentationTest_null() {
        assertNull(customerMapper.mapToUserRepresentation(null));
    }

    @Test
    void mapUserToUserInfoTest_success() {
        UserRepresentation userRepresentation = TestData.getUserRepresentation();

        UserInfo userInfo = customerMapper.mapUserToUserInfo(userRepresentation);

        AssertionUtil.assertUserDetails(userRepresentation, userInfo);
    }

    @Test
    void mapUserToUserInfoTest_null() {
        assertNull(customerMapper.mapUserToUserInfo(null));
    }

    @Test
    void mapToRegistrationResponseTest_success() {
        UserRepresentation userRepresentation = TestData.getUserRepresentation();

        RegistrationResponse registrationResponse = customerMapper.mapToRegistrationResponse(userRepresentation);

        AssertionUtil.assertRegistrationResponse(userRepresentation, registrationResponse);
    }

    @Test
    void mapToRegistrationResponseTest_null() {
        assertNull(customerMapper.mapToRegistrationResponse(null));
    }

}
