package com.swiftwheelshubreactive.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UserUpdateRequest(
        String username,
        String firstName,
        String email,
        String lastName,
        String address,
        LocalDate dateOfBirth
) {
}
