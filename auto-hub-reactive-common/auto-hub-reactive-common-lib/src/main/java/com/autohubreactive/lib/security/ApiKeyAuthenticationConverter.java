package com.autohubreactive.lib.security;

import com.autohubreactive.lib.util.ServerRequestUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@ConditionalOnProperty(prefix = "apikey", name = "secret")
public class ApiKeyAuthenticationConverter implements ServerAuthenticationConverter {

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.justOrEmpty(ServerRequestUtil.getApiKeyHeader(exchange.getRequest()))
                .map(apikey -> getApiKeyAuthenticationToken(exchange, apikey));
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
