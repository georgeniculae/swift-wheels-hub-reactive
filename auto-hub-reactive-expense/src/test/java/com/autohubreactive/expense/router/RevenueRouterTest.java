package com.autohubreactive.expense.router;

import com.autohubreactive.dto.invoice.RevenueResponse;
import com.autohubreactive.expense.handler.RevenueHandler;
import com.autohubreactive.expense.util.TestUtil;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = RevenueRouter.class)
class RevenueRouterTest {

    private static final String PATH = "/revenues";

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private RevenueHandler revenueHandler;

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findAllRevenuesTest_success() {
        RevenueResponse revenueResponse =
                TestUtil.getResourceAsJson("/data/RevenueResponse.json", RevenueResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(List.of(revenueResponse));

        when(revenueHandler.findAllRevenues(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<RevenueResponse> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(RevenueResponse.class)
                .getResponseBody();

        responseBody.as(StepVerifier::create)
                .expectNext(revenueResponse)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void findAllRevenuesTest_unauthorized() {
        RevenueResponse revenueResponse =
                TestUtil.getResourceAsJson("/data/RevenueResponse.json", RevenueResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(List.of(revenueResponse));

        when(revenueHandler.findAllRevenues(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void getTotalAmountTest_success() {
        RevenueResponse revenueResponse =
                TestUtil.getResourceAsJson("/data/RevenueResponse.json", RevenueResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(List.of(revenueResponse));

        when(revenueHandler.getTotalAmount(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<RevenueResponse> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/total")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(RevenueResponse.class)
                .getResponseBody();

        responseBody.as(StepVerifier::create)
                .expectNext(revenueResponse)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void getTotalAmountTest_unauthorized() {
        RevenueResponse revenueResponse =
                TestUtil.getResourceAsJson("/data/RevenueResponse.json", RevenueResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(List.of(revenueResponse));

        when(revenueHandler.getTotalAmount(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/total")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findRevenuesByDateTest_success() {
        RevenueResponse revenueResponse =
                TestUtil.getResourceAsJson("/data/RevenueResponse.json", RevenueResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(List.of(revenueResponse));

        when(revenueHandler.findRevenuesByDate(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<RevenueResponse> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/{date}", "2023-09-25")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(RevenueResponse.class)
                .getResponseBody();

        responseBody.as(StepVerifier::create)
                .expectNext(revenueResponse)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void findRevenuesByDateTest_unauthorized() {
        RevenueResponse revenueResponse =
                TestUtil.getResourceAsJson("/data/RevenueResponse.json", RevenueResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(List.of(revenueResponse));

        when(revenueHandler.findRevenuesByDate(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/{date}", "2023-09-25")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

}
