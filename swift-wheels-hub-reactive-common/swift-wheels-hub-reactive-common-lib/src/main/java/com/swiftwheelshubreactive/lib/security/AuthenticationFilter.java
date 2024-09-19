package com.swiftwheelshubreactive.lib.security;

import com.swiftwheelshubreactive.lib.util.ServerRequestUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.List;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "apikey", name = "secret")
public class AuthenticationFilter implements WebFilter {

    private final ReactiveAuthenticationManager reactiveAuthenticationManager;

    @Override
    @NonNull
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        return Mono.just(exchange)
                .flatMap(serverWebExchange -> {
                    String apikey = ServerRequestUtil.getApiKeyHeader(serverWebExchange.getRequest());

                    if (StringUtils.isBlank(apikey)) {
                        return chain.filter(serverWebExchange);
                    }

                    return authenticate(serverWebExchange, chain, apikey);
                });
    }

    private Mono<Void> authenticate(ServerWebExchange serverWebExchange, WebFilterChain chain, String apikey) {
        return Mono.just(getApiKeyAuthenticationToken(serverWebExchange, apikey))
                .flatMap(reactiveAuthenticationManager::authenticate)
                .flatMap(authentication -> {
                    SecurityContext securityContext = getSecurityContext(authentication);
                    Context updatedContext = ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext));

                    return chain.filter(serverWebExchange).contextWrite(updatedContext);
                });
    }

    private SecurityContext getSecurityContext(Authentication authentication) {
        SecurityContext securityContext = new SecurityContextImpl(authentication);
        securityContext.setAuthentication(authentication);

        return securityContext;
    }

    private ApiKeyAuthenticationToken getApiKeyAuthenticationToken(ServerWebExchange exchange, String apiKey) {
        List<SimpleGrantedAuthority> roles = getRoles(ServerRequestUtil.getRolesHeader(exchange.getRequest()));

        return new ApiKeyAuthenticationToken(roles, apiKey);
    }

    private List<SimpleGrantedAuthority> getRoles(List<String> roles) {
        return roles.stream()
                .filter(ObjectUtils::isNotEmpty)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

}
