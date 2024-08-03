package com.swiftwheelshubreactive.apigateway.filter.global;

import com.swiftwheelshubreactive.dto.RequestValidationReport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestValidatorFilterTest {

    @InjectMocks
    private RequestValidatorFilter requestValidatorFilter;

    @Mock
    private GatewayFilterChain chain;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Test
    @SuppressWarnings("unchecked")
    void filterTest_success() {
        ReflectionTestUtils.setField(requestValidatorFilter, "requestValidatorUrl", "test");

        MockServerHttpRequest request = MockServerHttpRequest.get("/agency/rental-offices/{id}", 1)
                .accept(MediaType.APPLICATION_JSON)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.builder(request).build();

        RequestValidationReport requestValidationReport = new RequestValidationReport("");

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), any(String[].class))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(RequestValidationReport.class)).thenReturn(Mono.just(requestValidationReport));
        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        requestValidatorFilter.filter(exchange, chain)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    @SuppressWarnings("unchecked")
    void filterTest_validationReportWithErrors() {
        ReflectionTestUtils.setField(requestValidatorFilter, "requestValidatorUrl", "test");

        MockServerHttpRequest request = MockServerHttpRequest.get("/agency/rental-offices/{id}", 1)
                .accept(MediaType.APPLICATION_JSON)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.builder(request).build();

        RequestValidationReport requestValidationReport = new RequestValidationReport("error");

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), any(String[].class))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(RequestValidationReport.class)).thenReturn(Mono.just(requestValidationReport));

        requestValidatorFilter.filter(exchange, chain)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void filterTest_definitionPath() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/agency/definition/swagger-ui.html")
                .accept(MediaType.APPLICATION_JSON)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.builder(request).build();

        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        requestValidatorFilter.filter(exchange, chain)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

}
