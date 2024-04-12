package com.swiftwheelshubreactive.lib.security;

import com.swiftwheelshubreactive.lib.util.ServerRequestUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "apikey", name = "secret")
public class LoadSecurityContextRepository extends WebSessionServerSecurityContextRepository {

    @Value("${apikey.secret}")
    private String apiKeySecret;
    private final ReactiveAuthenticationManager reactiveAuthenticationManager;

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        return Mono.justOrEmpty(getApiKeyHeader(exchange))
                .filter(apiKey -> apiKeySecret.equals(apiKey))
                .map(apiKey -> getApiKeyAuthenticationToken(exchange, apiKey))
                .flatMap(reactiveAuthenticationManager::authenticate)
                .map(SecurityContextImpl::new);
    }

    private String getApiKeyHeader(ServerWebExchange exchange) {
        return exchange.getRequest()
                .getHeaders()
                .getFirst("X-API-KY");
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
