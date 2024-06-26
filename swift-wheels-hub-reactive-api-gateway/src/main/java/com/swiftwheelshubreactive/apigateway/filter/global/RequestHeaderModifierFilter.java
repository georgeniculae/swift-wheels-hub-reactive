package com.swiftwheelshubreactive.apigateway.filter.global;

import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshubreactive.apigateway.security.JwtAuthenticationTokenConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestHeaderModifierFilter implements GlobalFilter, Ordered {

    private static final String X_API_KEY_HEADER = "X-API-KEY";

    private static final String X_USERNAME = "X-USERNAME";

    private static final String X_ROLES = "X-ROLES";

    private static final String REGISTER_PATH = "/register";

    private static final String DEFINITION_PATH = "/definition";

    @Value("${apikey-secret}")
    private String apikey;

    private final JwtAuthenticationTokenConverter jwtAuthenticationTokenConverter;

    private final NimbusReactiveJwtDecoder nimbusReactiveJwtDecoder;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return modifyHeaders(exchange)
                .flatMap(chain::filter)
                .onErrorMap(e -> {
                    log.error("Error while trying to log headers: {}", e.getMessage());

                    return new SwiftWheelsHubException(e.getMessage());
                });
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private Mono<ServerWebExchange> modifyHeaders(ServerWebExchange exchange) {
        return Mono.zip(
                getUsername(exchange.getRequest()),
                getRoles(exchange.getRequest()),
                (username, authorities) -> mutateServerWebExchange(exchange, username, authorities)
        );
    }

    private Mono<String> getUsername(ServerHttpRequest request) {
        return Mono.just(request)
                .filter(this::doesPathContainPattern)
                .flatMap(serverHttpRequest -> nimbusReactiveJwtDecoder.decode(getAuthorizationToken(serverHttpRequest)))
                .map(jwtAuthenticationTokenConverter::extractUsername)
                .switchIfEmpty(Mono.defer(() -> Mono.just(StringUtils.EMPTY)));
    }

    private Mono<List<String>> getRoles(ServerHttpRequest request) {
        return Mono.just(request)
                .filter(this::doesPathContainPattern)
                .flatMap(serverHttpRequest -> nimbusReactiveJwtDecoder.decode(getAuthorizationToken(serverHttpRequest)))
                .flatMapMany(jwtAuthenticationTokenConverter::extractGrantedAuthorities)
                .map(GrantedAuthority::getAuthority)
                .collectList()
                .switchIfEmpty(Mono.defer(() -> Mono.just(List.of())));
    }

    private boolean doesPathContainPattern(ServerHttpRequest serverHttpRequest) {
        String path = serverHttpRequest.getPath().value();

        return !path.contains(REGISTER_PATH) && !path.contains(DEFINITION_PATH);
    }

    private String getAuthorizationToken(ServerHttpRequest request) {
        return Optional.ofNullable(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .orElseThrow(() -> new SwiftWheelsHubResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Authorization header is missing"
                        )
                )
                .substring(7);
    }

    private ServerWebExchange mutateServerWebExchange(ServerWebExchange exchange,
                                                      String username, List<String> authorities) {
        return exchange.mutate()
                .request(mutateHeaders(username, authorities))
                .build();
    }

    private Consumer<ServerHttpRequest.Builder> mutateHeaders(String username, List<String> roles) {
        return requestBuilder -> {
            requestBuilder.header(X_API_KEY_HEADER, apikey);

            if (ObjectUtils.isNotEmpty(username)) {
                requestBuilder.header(X_USERNAME, username);
            }

            if (ObjectUtils.isNotEmpty(roles)) {
                requestBuilder.header(X_ROLES, roles.toArray(new String[]{}));
            }

            requestBuilder.headers(headers -> headers.remove(HttpHeaders.AUTHORIZATION));
        };
    }

}
