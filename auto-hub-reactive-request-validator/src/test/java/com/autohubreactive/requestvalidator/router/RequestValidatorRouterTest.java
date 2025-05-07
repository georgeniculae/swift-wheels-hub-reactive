package com.autohubreactive.requestvalidator.router;

import com.autohubreactive.dto.common.RequestValidationReport;
import com.autohubreactive.requestvalidator.handler.RequestValidatorHandler;
import com.autohubreactive.requestvalidator.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
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
@ContextConfiguration(classes = RequestValidatorRouter.class)
class RequestValidatorRouterTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private RequestValidatorHandler requestValidatorHandler;

    @Test
    void routeRequestTest_success() {
        RequestValidationReport validationReport =
                TestUtil.getResourceAsJson("/data/RequestValidationReport.json", RequestValidationReport.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(validationReport);

        when(requestValidatorHandler.validateRequest(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<RequestValidationReport> responseBody = webTestClient.post()
                .uri("/validate")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(RequestValidationReport.class)
                .getResponseBody();

        responseBody.as(StepVerifier::create)
                .expectNext(validationReport)
                .verifyComplete();
    }

    @Test
    void routeRequestTest_missingRequestBody() {
        Mono<ServerResponse> serverResponse = ServerResponse.badRequest().build();

        when(requestValidatorHandler.validateRequest(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.post()
                .uri("/validate")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void repopulateRedisWithSwaggerFilesTest_success() {
        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(true);

        when(requestValidatorHandler.repopulateRedisWithSwaggerFiles(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<Boolean> responseBody = webTestClient.post()
                .uri("/invalidate/{microserviceName}", "expense")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(Boolean.class)
                .getResponseBody();

        responseBody.as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void repopulateRedisWithSwaggerFilesTest_emptyPathVariable_notFound() {
        Mono<ServerResponse> serverResponse = ServerResponse.notFound().build();

        when(requestValidatorHandler.repopulateRedisWithSwaggerFiles(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.post()
                .uri("/invalidate/{microserviceName}", "")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

}
