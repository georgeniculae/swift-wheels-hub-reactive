package com.swiftwheelshub.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.Map;

@Builder
public record IncomingRequestDetails(
        @NotBlank(message = "Path cannot be blank")
        String path,

        @NotBlank(message = "Method cannot be blank")
        String method,

        Map<String, String> headers,

        Map<String, String> queryParams,

        String body
) {
}
