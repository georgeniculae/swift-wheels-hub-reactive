package com.swiftwheelshubreactive.apigateway.filter.global;

import com.swiftwheelshubreactive.dto.IncomingRequestDetails;
import com.swiftwheelshubreactive.dto.RequestValidationReport;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshubreactive.lib.exceptionhandling.ExceptionUtil;
import com.swiftwheelshubreactive.lib.retry.RetryHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestValidatorFilter implements GlobalFilter, Ordered {

    private static final String DEFINITION = "definition";
    private static final String ACTUATOR = "actuator";
    private static final String FALLBACK = "fallback";
    private final WebClient webClient;
    private final RetryHandler retryHandler;

    @Value("${request-validator-url}")
    private String requestValidatorUrl;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return Mono.just(exchange)
                .flatMap(serverWebExchange -> forwardRequest(chain, serverWebExchange))
                .onErrorResume(e -> {
                    log.error("Error while validating request: {}", e.getMessage());

                    HttpStatusCode statusCode = ExceptionUtil.extractExceptionStatusCode(e);
                    exchange.getResponse().setStatusCode(statusCode);

                    return exchange.getResponse().setComplete();
                });
    }

    @Override
    public int getOrder() {
        return 1;
    }

    private Mono<Void> forwardRequest(GatewayFilterChain chain, ServerWebExchange serverWebExchange) {
        if (isRequestValidatable(serverWebExchange.getRequest())) {
            return filterValidatedRequest(serverWebExchange, chain);
        }

        return chain.filter(serverWebExchange);
    }

    private boolean isRequestValidatable(ServerHttpRequest serverHttpRequest) {
        String path = serverHttpRequest.getPath().value();

        return !path.contains(DEFINITION) && !path.contains(ACTUATOR) && !path.contains(FALLBACK);
    }

    private Mono<Void> filterValidatedRequest(ServerWebExchange exchange, GatewayFilterChain chain) {
        return getIncomingRequestDetails(exchange.getRequest())
                .flatMap(this::getValidationReport)
                .flatMap(requestValidationReport -> validateResponse(exchange, chain, requestValidationReport));
    }

    private Mono<IncomingRequestDetails> getIncomingRequestDetails(ServerHttpRequest request) {
        return request.getBody()
                .map(dataBuffer -> dataBuffer.toString(StandardCharsets.UTF_8))
                .reduce(StringUtils.EMPTY, (current, next) -> current + next)
                .map(bodyAsString -> getIncomingRequestDetails(request, bodyAsString));
    }

    private IncomingRequestDetails getIncomingRequestDetails(ServerHttpRequest request, String bodyAsString) {
        return IncomingRequestDetails.builder()
                .path(request.getPath().value())
                .method(request.getMethod().name())
                .headers(request.getHeaders().toSingleValueMap())
                .queryParams(request.getQueryParams().toSingleValueMap())
                .body(bodyAsString)
                .build();
    }

    private Mono<RequestValidationReport> getValidationReport(IncomingRequestDetails incomingRequestDetails) {
        return webClient.post()
                .uri(requestValidatorUrl)
                .bodyValue(incomingRequestDetails)
                .retrieve()
                .bodyToMono(RequestValidationReport.class)
                .retryWhen(retryHandler.retry())
                .onErrorMap(e -> {
                    log.error("Error while sending request to validator: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    private Mono<Void> validateResponse(ServerWebExchange exchange, GatewayFilterChain chain, RequestValidationReport requestValidationReport) {
        if (ObjectUtils.isEmpty(requestValidationReport.errorMessage())) {
            return chain.filter(exchange);
        }

        return Mono.error(
                new SwiftWheelsHubResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        requestValidationReport.errorMessage()
                )
        );
    }

}
