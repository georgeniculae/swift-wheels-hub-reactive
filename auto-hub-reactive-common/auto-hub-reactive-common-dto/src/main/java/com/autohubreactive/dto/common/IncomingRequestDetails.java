package com.autohubreactive.dto.common;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.List;

@Builder
public record IncomingRequestDetails(
        @NotBlank(message = "Path cannot be blank")
        String path,

        @NotBlank(message = "Method cannot be blank")
        String method,

        List<Header> headers,

        List<QueryParam> queryParams,

        String body
) {

    @Override
    public String toString() {
        return "IncomingRequestDetails{" + "\n" +
                "path=" + path + "\n" +
                "method=" + method + "\n" +
                "headers=" + headers + "\n" +
                "queryParams=" + queryParams + "\n" +
                "body=" + body + "\n" +
                "}";
    }

    @Builder
    public record Header(
            String name,
            List<String> values
    ) {
    }

    @Builder
    public record QueryParam(
            String name,
            List<String> value
    ) {
    }

}
