package com.swiftwheelshubreactive.requestvalidator.router;

import com.swiftwheelshubreactive.dto.RequestValidationReport;
import com.swiftwheelshubreactive.requestvalidator.handler.RequestValidatorHandler;
import com.swiftwheelshubreactive.requestvalidator.util.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = RequestValidatorRouter.class)
class RequestValidatorRouterTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private RequestValidatorHandler requestValidatorHandler;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void routeRequestTest_success() {
        RequestValidationReport validationReport =
                TestUtils.getResourceAsJson("/data/RequestValidationReport.json", RequestValidationReport.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(validationReport);

        when(requestValidatorHandler.validateRequest(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri("/validate")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody();
    }

    @Test
    @WithAnonymousUser
    void routeRequestTest_unauthorized() {
        RequestValidationReport validationReport =
                TestUtils.getResourceAsJson("/data/RequestValidationReport.json", RequestValidationReport.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(validationReport);

        when(requestValidatorHandler.validateRequest(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri("/validate")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized()
                .expectBody();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void repopulateRedisWithSwaggerFilesTest_success() {
        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(true);

        when(requestValidatorHandler.repopulateRedisWithSwaggerFiles(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri("/invalidate/{microserviceName}", "expense")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody();
    }

    @Test
    @WithAnonymousUser
    void repopulateRedisWithSwaggerFilesTest_unauthorized() {
        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(true);

        when(requestValidatorHandler.repopulateRedisWithSwaggerFiles(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri("/invalidate/{microserviceName}", "expense")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized()
                .expectBody();
    }

}
