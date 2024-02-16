package com.swiftwheelshub.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record RegisterRequest(
        String username,
        String password,
        String firstName,
        String lastName,
        String email,
        String address,
        LocalDate dateOfBirth,
        boolean needsEmailVerification
) {

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "username='" + username + "\n" +
                "password='" + password + "\n" +
                "firstName='" + firstName + "\n" +
                "lastName='" + lastName + "\n" +
                "email='" + email + "\n" +
                "address='" + address + "\n" +
                "dateOfBirth=" + dateOfBirth + "\n" +
                "needsEmailVerification" + needsEmailVerification + "\n" +
                "}";
    }

}
