package com.swiftwheelshub.dto;

import lombok.Builder;
import org.springframework.lang.NonNull;

import java.util.Map;

@Builder
public record IncomingRequestDetails(
        @NonNull
        String path,

        @NonNull
        String method,

        Map<String, String> headers,

        Map<String, String> queryParams,

        String body
) {
}
