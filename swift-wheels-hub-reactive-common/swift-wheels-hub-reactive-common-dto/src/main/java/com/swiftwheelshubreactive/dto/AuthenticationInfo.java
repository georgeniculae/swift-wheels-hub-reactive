package com.swiftwheelshubreactive.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record AuthenticationInfo(
        String apikey,
        String username,
        List<String> roles
) {
}
