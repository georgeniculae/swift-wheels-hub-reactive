package com.swiftwheelshub.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record RegistrationResponse(
        String username,
        String firstName,
        String lastName,
        String email,
        String address,
        LocalDate dateOfBirth,
        String registrationDate
) {
}
