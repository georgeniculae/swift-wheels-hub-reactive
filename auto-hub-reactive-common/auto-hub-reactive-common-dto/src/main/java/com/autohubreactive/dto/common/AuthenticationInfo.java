package com.autohubreactive.dto.common;

import lombok.Builder;

import java.util.List;

@Builder
public record AuthenticationInfo(
        String apikey,
        String username,
        String email,
        List<String> roles
) {
}
