package com.autohubreactive.apigateway.filter.global;

import com.autohubreactive.lib.exceptionhandling.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestBodyModifierFilter implements GlobalFilter, Ordered {

    private final ModifyRequestBodyGatewayFilterFactory modifyRequestBodyGatewayFilterFactory;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return modifyRequestBodyGatewayFilterFactory.apply(getRewriteFunction())
                .filter(exchange, chain)
                .onErrorResume(e -> {
                    log.error("Error while trying to modify body: {}", e.getMessage());

                    HttpStatusCode statusCode = ExceptionUtil.extractExceptionStatusCode(e);
                    ServerHttpResponse response = exchange.getResponse();
                    response.setStatusCode(statusCode);

                    return response.setComplete();
                });

    }

    @Override
    public int getOrder() {
        return -1;
    }

    private ModifyRequestBodyGatewayFilterFactory.Config getRewriteFunction() {
        return new ModifyRequestBodyGatewayFilterFactory.Config()
                .setRewriteFunction(
                        byte[].class,
                        byte[].class,
                        (_, requestBody) -> Mono.justOrEmpty(requestBody)
                );
    }

}
