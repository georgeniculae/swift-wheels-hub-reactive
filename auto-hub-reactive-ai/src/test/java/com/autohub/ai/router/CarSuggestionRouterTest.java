package com.autohub.ai.router;

import com.autohub.ai.handler.CarSuggestionHandler;
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
@ContextConfiguration(classes = CarSuggestionRouter.class)
class CarSuggestionRouterTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CarSuggestionHandler carSuggestionHandler;

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void getChatPromptTest_success() {
        String output = "Test";
        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(output);

        when(carSuggestionHandler.getChatOutput(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<String> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/car-suggestion")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(String.class)
                .getResponseBody();

        responseBody.as(StepVerifier::create)
                .expectNext(output)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void getChatPromptTest_unauthorized() {
        String output = "Test";
        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(output);

        when(carSuggestionHandler.getChatOutput(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/car-suggestion")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

}
