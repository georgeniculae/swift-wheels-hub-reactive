package com.autohubreactive.apigateway.router;

import com.autohubreactive.apigateway.handler.FallbackHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = FallbackRouter.class)
class FallbackRouterTest {

    private static final String FALLBACK_PATH = "/fallback";

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private FallbackHandler fallbackHandler;

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void routeFallbackTest_success() {
        String serviceUnavailableMessage = "Service unavailable";
        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(serviceUnavailableMessage);

        when(fallbackHandler.fallback(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<String> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(FALLBACK_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(String.class)
                .getResponseBody();

        responseBody.as(StepVerifier::create)
                .expectNext(serviceUnavailableMessage)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void routeFallbackTest_unauthorized() {
        String serviceUnavailableMessage = "Service unavailable";
        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(serviceUnavailableMessage);

        when(fallbackHandler.fallback(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(FALLBACK_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

}
