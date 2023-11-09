package com.carrental.customer.util;

import com.carrental.document.dto.CurrentUserDto;
import com.carrental.document.model.Role;
import com.carrental.document.model.User;
import com.carrental.dto.UserDto;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils {

    public static void assertCurrentUser(User user, CurrentUserDto currentUserDto) {
        assertEquals(user.getUsername(), currentUserDto.username());
        assertEquals(user.getPassword(), currentUserDto.password());
        assertEquals(user.getRole(), currentUserDto.role());
        assertEquals(user.getFirstName(), currentUserDto.firstName());
        assertEquals(user.getLastName(), currentUserDto.lastName());
        assertEquals(user.getEmail(), currentUserDto.email());
        assertEquals(user.isAccountNonExpired(), currentUserDto.accountNonExpired());
        assertEquals(user.isAccountNonExpired(), currentUserDto.accountNonExpired());
        assertEquals(user.isCredentialsNonExpired(), currentUserDto.credentialsNonExpired());
        assertEquals(user.getAuthorities(), currentUserDto.authorities());
    }

    public static void assertCurrentUser(CurrentUserDto currentUserDto, User user) {
        assertEquals(user.getUsername(), currentUserDto.username());
        assertEquals(user.getPassword(), currentUserDto.password());
        assertEquals(user.getRole(), currentUserDto.role());
        assertEquals(user.getFirstName(), currentUserDto.firstName());
        assertEquals(user.getLastName(), currentUserDto.lastName());
        assertEquals(user.getEmail(), currentUserDto.email());
    }

    public static void assertUser(User user, UserDto userDto) {
        assertEquals(user.getUsername(), userDto.getUsername());
        assertEquals(user.getPassword(), userDto.getPassword());
        assertRole(user.getRole(), Optional.ofNullable(userDto.getRole()).orElseThrow());
        assertEquals(user.getFirstName(), userDto.getFirstName());
        assertEquals(user.getLastName(), userDto.getLastName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    private static void assertRole(Role role, UserDto.RoleEnum roleEnum) {
        assertEquals(role.getName(), roleEnum.getValue());
    }

}
