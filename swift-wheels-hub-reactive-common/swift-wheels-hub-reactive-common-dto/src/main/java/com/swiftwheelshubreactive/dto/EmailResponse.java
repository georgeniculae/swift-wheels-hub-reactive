package com.swiftwheelshubreactive.dto;

import lombok.Builder;

@Builder
public record EmailResponse(
        int statusCode,
        String body
) {
}
