package com.swiftwheelshubreactive.dto;

import lombok.Builder;

@Builder
public record CustomerInfo(
        String username,
        String email
) {
}
