package com.swiftwheelshub.cloudgateway.filter.global;

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

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return Mono.just(exchange)
                .map(webExchange -> webExchange.getRequest().getHeaders())
                .doOnNext(this::logHeaders)
                .flatMap(httpHeaders -> chain.filter(exchange));
    }

    @Override
    public int getOrder() {
        return 2;
    }

    private void logHeaders(HttpHeaders httpHeaders) {
        log.info("Request headers: ");

        httpHeaders.toSingleValueMap()
                .forEach((header, value) -> log.info(header + ": " + value));
    }

}
