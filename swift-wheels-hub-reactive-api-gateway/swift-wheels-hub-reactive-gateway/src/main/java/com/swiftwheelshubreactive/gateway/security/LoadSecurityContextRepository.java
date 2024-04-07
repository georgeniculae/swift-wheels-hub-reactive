package com.swiftwheelshubreactive.gateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class LoadSecurityContextRepository extends WebSessionServerSecurityContextRepository {

    private final static String BEARER = "Bearer ";
    private final ReactiveAuthenticationManager reactiveAuthenticationManager;

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        return Mono.justOrEmpty(getAuthorizationHeader(exchange))
                .filter(authorization -> authorization.startsWith(BEARER))
                .map(this::getBearerTokenAuthenticationToken)
                .flatMap(reactiveAuthenticationManager::authenticate)
                .map(SecurityContextImpl::new);
    }

    private String getAuthorizationHeader(ServerWebExchange exchange) {
        return exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);
    }

    private BearerTokenAuthenticationToken getBearerTokenAuthenticationToken(String authorizationToken) {
        String jwtToken = authorizationToken.substring(BEARER.length());

        return new BearerTokenAuthenticationToken(jwtToken);
    }

}
