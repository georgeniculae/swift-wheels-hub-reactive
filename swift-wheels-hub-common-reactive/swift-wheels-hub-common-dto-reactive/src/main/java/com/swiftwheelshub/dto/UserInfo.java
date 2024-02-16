package com.swiftwheelshub.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record UserInfo(
        String id,
        String username,
        String firstName,
        String lastName,
        String email,
        String address,
        LocalDate dateOfBirth,
        List<String> roles) {
}
