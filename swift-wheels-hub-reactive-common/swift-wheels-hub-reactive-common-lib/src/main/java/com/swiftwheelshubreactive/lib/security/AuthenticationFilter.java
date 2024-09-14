package com.swiftwheelshubreactive.lib.security;

import com.swiftwheelshubreactive.lib.util.ServerRequestUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "apikey", name = "secret")
public class AuthenticationFilter implements WebFilter {

    private static final String X_API_KEY = "X-API-KEY";
    private final ReactiveAuthenticationManager reactiveAuthenticationManager;

    @Value("${apikey.secret}")
    private String apiKeySecret;

    @Override
    @NonNull
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        return Mono.justOrEmpty(getApiKeyHeader(exchange))
                .map(apiKey -> getApiKeyAuthenticationToken(exchange, apiKey))
                .flatMap(reactiveAuthenticationManager::authenticate)
                .map(SecurityContextImpl::new)
                .flatMap(_ -> chain.filter(exchange))
                .switchIfEmpty(chain.filter(exchange));
    }

    private String getApiKeyHeader(ServerWebExchange exchange) {
        return exchange.getRequest()
                .getHeaders()
                .getFirst(X_API_KEY);
    }

    private ApiKeyAuthenticationToken getApiKeyAuthenticationToken(ServerWebExchange exchange, String apiKey) {
        List<SimpleGrantedAuthority> roles = getRoles(ServerRequestUtil.getRolesHeader(exchange.getRequest()));

        return new ApiKeyAuthenticationToken(roles, apiKey);
    }

    private List<SimpleGrantedAuthority> getRoles(List<String> roles) {
        return ObjectUtils.isEmpty(roles) ? List.of() : getRoleList(roles);
    }

    private List<SimpleGrantedAuthority> getRoleList(List<String> roles) {
        return roles.stream()
                .filter(ObjectUtils::isNotEmpty)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

}
