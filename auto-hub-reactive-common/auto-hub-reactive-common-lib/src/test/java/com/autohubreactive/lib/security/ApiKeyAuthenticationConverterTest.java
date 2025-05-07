package com.autohubreactive.lib.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ApiKeyAuthenticationConverterTest {

    @InjectMocks
    private ApiKeyAuthenticationConverter apiKeyAuthenticationConverter;

    @Test
    void convertTest_success() {
        String apikey = "apikey";
        String role = "user";

        MockServerHttpRequest request = MockServerHttpRequest.get("/agency/rental-offices/{id}", 1)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-API-KEY", apikey)
                .header("X-ROLES", role)
                .build();

        MockServerWebExchange serverWebExchange = new MockServerWebExchange.Builder(request).build();

        apiKeyAuthenticationConverter.convert(serverWebExchange)
                .as(StepVerifier::create)
                .expectNextMatches(authentication -> apikey.equals(authentication.getPrincipal().toString()))
                .verifyComplete();
    }

    @Test
    void convertTest_emptyAuthentication() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/customers/register")
                .accept(MediaType.APPLICATION_JSON)
                .build();

        MockServerWebExchange serverWebExchange = new MockServerWebExchange.Builder(request).build();

        apiKeyAuthenticationConverter.convert(serverWebExchange)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

}
