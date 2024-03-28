package com.swiftwheelshubreactive.dto;

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

    @Override
    public String toString() {
        return "IncomingRequestDetails{" + "\n" +
                "path=" + path + "\n" +
                "method=" + method + "\n" +
                "headers='" + headers + "\n" +
                "queryParams=" + queryParams + "\n" +
                "body=" + body + "\n" +
                "}";
    }

}
