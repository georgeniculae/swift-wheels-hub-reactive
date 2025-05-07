package com.autohubreactive.expense.handler;

import com.autohubreactive.dto.invoice.RevenueResponse;
import com.autohubreactive.expense.service.RevenueService;
import com.autohubreactive.expense.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RevenueHandlerTest {

    @InjectMocks
    private RevenueHandler revenueHandler;

    @Mock
    private RevenueService revenueService;

    @Test
    void findAllRevenuesTest_success() {
        RevenueResponse revenueResponse =
                TestUtil.getResourceAsJson("/data/RevenueResponse.json", RevenueResponse.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(revenueService.findAllRevenues()).thenReturn(Flux.just(revenueResponse));

        revenueHandler.findAllRevenues(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void findAllRevenuesTest_noResultReturned() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(revenueService.findAllRevenues()).thenReturn(Flux.empty());

        revenueHandler.findAllRevenues(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void findRevenuesByDateTest_success() {
        RevenueResponse revenueResponse =
                TestUtil.getResourceAsJson("/data/RevenueResponse.json", RevenueResponse.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("dateOfRevenue", "2023-09-27")
                .build();

        when(revenueService.findRevenuesByDate(anyString())).thenReturn(Flux.just(revenueResponse));

        revenueHandler.findRevenuesByDate(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void findRevenuesByDateTest_noResultReturned() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("dateOfRevenue", "2023-09-27")
                .build();

        when(revenueService.findRevenuesByDate(anyString())).thenReturn(Flux.empty());

        revenueHandler.findRevenuesByDate(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void getTotalAmountTest_success() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(revenueService.getTotalAmount()).thenReturn(Mono.just(BigDecimal.valueOf(500)));

        revenueHandler.getTotalAmount(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

}
