package com.swiftwheelshubreactive.gateway.filter.global;

import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class RequestHeadersLoggerFilter implements GlobalFilter, Ordered {

    private static final String X_API_KEY = "X-API-KEY";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return Mono.just(exchange)
                .map(webExchange -> webExchange.getRequest().getHeaders())
                .doOnNext(this::logHeaders)
                .flatMap(httpHeaders -> chain.filter(exchange))
                .onErrorMap(e -> {
                    log.error("Error while trying to log headers: {}", e.getMessage());

                    return new SwiftWheelsHubException(e);
                });
    }

    @Override
    public int getOrder() {
        return 2;
    }

    private void logHeaders(HttpHeaders httpHeaders) {
        log.info("Request headers: ");

        httpHeaders.forEach((header, value) -> {
            if (!X_API_KEY.equals(header)) {
                log.info("{}: {}", header, value);
            }
        });
    }

}
