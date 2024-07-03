package com.swiftwheelshubreactive.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record RequestDetails(
        String apikey,
        List<String> roles
) {
}
