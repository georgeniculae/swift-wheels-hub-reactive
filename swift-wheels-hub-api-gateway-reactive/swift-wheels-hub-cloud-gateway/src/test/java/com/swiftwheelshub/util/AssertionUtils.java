package com.swiftwheelshub.util;

import com.swiftwheelshub.dto.UserDto;
import com.swiftwheelshub.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils {

    public static void assertUser(UserDto userDto, User user) {
        assertEquals(userDto.getUsername(), user.getUsername());
        assertEquals(userDto.getPassword(), user.getPassword());
        assertEquals(userDto.getEmail(), user.getEmail());
        assertEquals(userDto.getFirstName(), user.getFirstName());
        assertEquals(userDto.getLastName(), user.getLastName());
        assertEquals(Optional.ofNullable((userDto.getRole())).orElseThrow().getValue(), user.getRole().getName());
    }

}
