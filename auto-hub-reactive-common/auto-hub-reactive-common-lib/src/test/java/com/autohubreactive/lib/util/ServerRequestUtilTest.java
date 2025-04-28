package com.autohubreactive.lib.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ServerRequestUtilTest {

    @Test
    void getQueryParamTest_success() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .queryParam("test", "test")
                .build();

        String queryParam = ServerRequestUtil.getQueryParam(serverRequest, "test");

        assertEquals("test", queryParam);
    }

    @Test
    void getApiKeyHeaderTest_success() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .header("X-API-KEY", "test")
                .build();

        String apiKeyHeader = ServerRequestUtil.getApiKeyHeader(serverRequest);

        assertEquals("test", apiKeyHeader);
    }

    @Test
    void getRolesHeaderTest_success() {
        LinkedMultiValueMap<String, String> linkedMultiValueMap = new LinkedMultiValueMap<>();
        linkedMultiValueMap.put("X-ROLES", List.of("test"));

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .headers(HttpHeaders.readOnlyHttpHeaders(linkedMultiValueMap))
                .build();

        List<String> roles = ServerRequestUtil.getRolesHeader(serverRequest);

        assertEquals("test", roles.getFirst());
    }

    @Test
    void getUsernameTest_success() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .header("X-USERNAME", "test")
                .build();

        String username = ServerRequestUtil.getUsername(serverRequest);

        assertEquals("test", username);
    }

}
