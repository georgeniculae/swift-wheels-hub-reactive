package com.swiftwheelshubreactive.apigateway.filter.global;

import com.swiftwheelshubreactive.dto.IncomingRequestDetails;
import com.swiftwheelshubreactive.dto.RequestValidationReport;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshubreactive.lib.exceptionhandling.ExceptionUtil;
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
import reactor.util.retry.Retry;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestValidatorFilter implements GlobalFilter, Ordered {

    private final static String API_KEY_HEADER = "X-API-KEY";
    private static final String DEFINITION = "definition";
    private static final String ACTUATOR = "actuator";
    private static final String FALLBACK = "/fallback";
    private final WebClient webClient;

    @Value("${apikey-secret}")
    private String apikeySecret;

    @Value("${request-validator-url}")
    private String requestValidatorUrl;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return Mono.just(exchange.getRequest())
                .filter(this::containsRightPath)
                .flatMap(this::getIncomingRequestDetails)
                .flatMap(this::getValidationReport)
                .flatMap(requestValidationReport -> filterRequest(exchange, chain, requestValidationReport))
                .switchIfEmpty(Mono.defer(() -> chain.filter(exchange)))
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

    private boolean containsRightPath(ServerHttpRequest serverHttpRequest) {
        String path = serverHttpRequest.getPath().value();

        return !path.contains(DEFINITION) && !path.contains(ACTUATOR) && !path.contains(FALLBACK);
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
                .header(API_KEY_HEADER, apikeySecret)
                .bodyValue(incomingRequestDetails)
                .retrieve()
                .bodyToMono(RequestValidationReport.class)
                .retryWhen(Retry.fixedDelay(5, Duration.ofSeconds(5)))
                .onErrorMap(e -> {
                    log.error("Error while sending request to validator: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    private Mono<Void> filterRequest(ServerWebExchange exchange, GatewayFilterChain chain, RequestValidationReport requestValidationReport) {
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
