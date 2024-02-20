package com.swiftwheelshub.customer.mapper;

import com.swiftwheelshub.customer.util.AssertionUtils;
import com.swiftwheelshub.customer.util.TestData;
import com.swiftwheelshub.customer.util.TestUtils;
import com.swiftwheelshub.dto.RegistrationResponse;
import com.swiftwheelshub.dto.UserInfo;
import com.swiftwheelshub.dto.UserUpdateRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    private final UserMapper userMapper = new UserMapperImpl();

    @Test
    void mapToUserRepresentationTest_success() {
        UserUpdateRequest userUpdateRequest =
                TestUtils.getResourceAsJson("/data/UserUpdateRequest.json", UserUpdateRequest.class);

        UserRepresentation userRepresentation = userMapper.mapToUserRepresentation(userUpdateRequest);

        AssertionUtils.assertUserRepresentation(userUpdateRequest, userRepresentation);
    }

    @Test
    void mapToUserRepresentationTest_null() {
        assertNull(userMapper.mapToUserRepresentation(null));
    }

    @Test
    void mapUserToUserDetailsTest_success() {
        UserRepresentation userRepresentation = TestData.getUserRepresentation();

        UserInfo userInfo = userMapper.mapUserToUserDetails(userRepresentation);

        AssertionUtils.assertUserDetails(userRepresentation, userInfo);
    }

    @Test
    void mapUserToUserDetailsTest_null() {
        assertNull(userMapper.mapUserToUserDetails(null));
    }

    @Test
    void mapToRegistrationResponseTest_success() {
        UserRepresentation userRepresentation = TestData.getUserRepresentation();

        RegistrationResponse registrationResponse = userMapper.mapToRegistrationResponse(userRepresentation);

        AssertionUtils.assertRegistrationResponse(userRepresentation, registrationResponse);
    }

    @Test
    void mapToRegistrationResponseTest_null() {
        assertNull(userMapper.mapToRegistrationResponse(null));
    }

}
