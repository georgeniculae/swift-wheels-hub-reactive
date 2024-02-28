package com.swiftwheelshubreactive.lib.security;

import com.swiftwheelshubreactive.exception.SwiftWheelsHubResponseStatusException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@ConditionalOnBean(name = "apiKeySecurityConfig")
public class ApiKeyAuthenticationConverter implements ServerAuthenticationConverter {

    private final static String API_KEY_HEADER = "X-API-KEY";

    @Value(("${authentication.secret}"))
    private String apiKeySecret;

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.just(exchange)
                .switchIfEmpty(Mono.error(new SwiftWheelsHubResponseStatusException(HttpStatus.BAD_REQUEST, "No matching API Key")))
                .flatMap(apiKey -> exchange.getPrincipal())
                .flatMap(principal -> {
                    Authentication authentication = (Authentication) principal;
                    String apikey = authentication.getPrincipal().toString();

                    if (apiKeySecret.equals(apikey)) {
                        new ApiKeyAuthenticationToken(getRoles(authentication), apikey);
                    }

                    throw new SwiftWheelsHubResponseStatusException(HttpStatus.BAD_REQUEST, "No matching API Key");
                });
    }

    private List<SimpleGrantedAuthority> getRoles(Authentication authentication) {
        return authentication.getAuthorities()
                .stream()
                .map(grantedAuthority -> new SimpleGrantedAuthority(grantedAuthority.getAuthority()))
                .toList();
    }

}
