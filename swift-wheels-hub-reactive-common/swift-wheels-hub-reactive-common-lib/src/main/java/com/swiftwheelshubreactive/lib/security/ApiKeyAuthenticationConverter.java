package com.swiftwheelshubreactive.lib.security;

import com.swiftwheelshubreactive.exception.SwiftWheelsHubResponseStatusException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@ConditionalOnBean(name = "apiKeySecurityConfig")
public class ApiKeyAuthenticationConverter implements ServerAuthenticationConverter {

    private final static String API_KEY_HEADER = "X-API-KEY";

    @Value(("${authentication.secret}"))
    private String apiKeySecret;

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange)
                .map(serverWebExchange -> serverWebExchange.getRequest().getHeaders().getFirst(API_KEY_HEADER))
                .filter(apiKey -> apiKeySecret.equals(apiKey))
                .switchIfEmpty(Mono.error(new SwiftWheelsHubResponseStatusException(HttpStatus.BAD_REQUEST, "No matching API Key")))
                .map(ApiKeyAuthenticationToken::new);
    }

}
