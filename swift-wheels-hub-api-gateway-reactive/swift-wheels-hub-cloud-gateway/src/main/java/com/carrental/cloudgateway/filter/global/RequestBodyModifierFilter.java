package com.carrental.cloudgateway.filter.global;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RequestBodyModifierFilter implements GlobalFilter, Ordered {

    private final ModifyRequestBodyGatewayFilterFactory modifyRequestBodyGatewayFilterFactory;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return modifyRequestBodyGatewayFilterFactory.apply(
                        new ModifyRequestBodyGatewayFilterFactory.Config()
                                .setRewriteFunction(
                                        byte[].class,
                                        byte[].class,
                                        (webExchange, requestBody) -> Mono.justOrEmpty(requestBody)
                                )
                )
                .filter(exchange, chain);
    }

    @Override
    public int getOrder() {
        return -1;
    }

}
