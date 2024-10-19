package com.swiftwheelshubreactive.dto;

import lombok.Builder;
import org.springframework.lang.NonNull;

import java.time.LocalDate;

@Builder
public record UserUpdateRequest(
        @NonNull
        String username,

        @NonNull
        String firstName,

        @NonNull
        String email,

        @NonNull
        String lastName,

        @NonNull
        String address,

        @NonNull
        LocalDate dateOfBirth
) {

    @Override
    public String toString() {
        return "UserUpdateRequest{" + "\n" +
                "username=" + username + "\n" +
                "email=" + email + "\n" +
                "firstName=" + firstName + "\n" +
                "lastName=" + lastName + "\n" +
                "address=" + address + "\n" +
                "dateOfBirth=" + dateOfBirth + "\n" +
                "}";
    }

}
