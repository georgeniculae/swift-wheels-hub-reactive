package com.autohubreactive.apigateway.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class FallbackHandlerTest {

    @InjectMocks
    private FallbackHandler fallbackHandler;

    @Test
    void fallbackTest_success() {
        MockServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        fallbackHandler.fallback(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is5xxServerError())
                .verifyComplete();
    }

}
