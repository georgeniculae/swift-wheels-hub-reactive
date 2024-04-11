package com.swiftwheelshubreactive.lib.security;

import com.swiftwheelshubreactive.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshubreactive.lib.util.ServerRequestUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
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

    @Value("${apikey.secret}")
    private String apiKeySecret;

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.just(exchange.getRequest())
                .map(serverHttpRequest -> {
                    String apiKey = ServerRequestUtil.getApiKeyHeader(serverHttpRequest);
                    List<String> roles = ServerRequestUtil.getRolesHeader(serverHttpRequest);

                    if (apiKeySecret.equals(apiKey)) {
                        return new ApiKeyAuthenticationToken(getRoles(roles), apiKey);
                    }

                    throw new SwiftWheelsHubResponseStatusException(HttpStatus.BAD_REQUEST, "No matching API Key");
                });
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
