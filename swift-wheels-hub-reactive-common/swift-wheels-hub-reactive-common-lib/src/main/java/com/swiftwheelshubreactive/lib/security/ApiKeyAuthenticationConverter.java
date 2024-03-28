package com.swiftwheelshubreactive.lib.security;

import com.swiftwheelshubreactive.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshubreactive.lib.util.ServerRequestUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
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

    @Value(("${apikey.secret}"))
    private String apiKeySecret;

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.fromSupplier(() -> {
            ServerHttpRequest request = exchange.getRequest();

            String apiKey = ServerRequestUtil.getApiKeyHeader(request);
            List<String> roles = ServerRequestUtil.getRolesHeader(request);

            if (apiKeySecret.equals(apiKey)) {
                return new ApiKeyAuthenticationToken(getRoles(roles), apiKey);
            }

            throw new SwiftWheelsHubResponseStatusException(HttpStatus.BAD_REQUEST, "No matching API Key");
        });
    }

    private List<SimpleGrantedAuthority> getRoles(List<String> roles) {
        return ObjectUtils.isEmpty(roles) ? List.of() : getRolesList(roles);
    }

    private List<SimpleGrantedAuthority> getRolesList(List<String> roles) {
        return roles.stream()
                .filter(ObjectUtils::isNotEmpty)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

}
