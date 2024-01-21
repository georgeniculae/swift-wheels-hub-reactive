package com.swiftwheelshub.lib.util;

import com.swiftwheelshub.dto.RegisterRequest;
import com.swiftwheelshub.dto.UserDto;
import com.swiftwheelshub.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils {

    public static void assertUser(RegisterRequest registerRequest, User user) {
        assertEquals(registerRequest.getUsername(), user.getUsername());
        assertEquals(registerRequest.getPassword(), user.getPassword());
        assertEquals(registerRequest.getFirstName(), user.getFirstName());
        assertEquals(registerRequest.getLastName(), user.getLastName());
        assertEquals(registerRequest.getEmail(), user.getEmail());
    }

    public static void assertUser(User customer, UserDto customerDto) {
        assertEquals(customer.getFirstName(), customerDto.getFirstName());
        assertEquals(customer.getLastName(), customerDto.getLastName());
        assertEquals(customer.getEmail(), customerDto.getEmail());
    }

}
