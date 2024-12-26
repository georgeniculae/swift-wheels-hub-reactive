package com.swiftwheelshubreactive.apigateway.filter.global;

import com.swiftwheelshubreactive.apigateway.security.JwtAuthenticationTokenConverter;
import com.swiftwheelshubreactive.dto.AuthenticationInfo;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshubreactive.lib.exceptionhandling.ExceptionUtil;
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
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
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
    private static final String REGISTER_PATH = "register";
    private static final String DEFINITION_PATH = "definition";
    private static final String FALLBACK = "fallback";
    private final JwtAuthenticationTokenConverter jwtAuthenticationTokenConverter;
    private final NimbusReactiveJwtDecoder nimbusReactiveJwtDecoder;

    @Value("${apikey-secret}")
    private String apikey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return Mono.just(exchange)
                .flatMap(serverWebExchange -> forwardRequest(exchange, chain, serverWebExchange))
                .onErrorResume(e -> {
                    log.error("Error while trying to mutate headers: {}", e.getMessage());

                    HttpStatusCode statusCode = ExceptionUtil.extractExceptionStatusCode(e);
                    exchange.getResponse().setStatusCode(statusCode);

                    return exchange.getResponse().setComplete();
                });
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private Mono<Void> forwardRequest(ServerWebExchange exchange, GatewayFilterChain chain, ServerWebExchange serverWebExchange) {
        if (isRequestValidatable(serverWebExchange.getRequest())) {
            return filterValidatedRequest(exchange, chain);
        }

        return chain.filter(exchange);
    }

    private boolean isRequestValidatable(ServerHttpRequest serverHttpRequest) {
        String path = serverHttpRequest.getPath().value();

        return !path.contains(REGISTER_PATH) && !path.contains(DEFINITION_PATH) && !path.contains(FALLBACK);
    }

    private Mono<Void> filterValidatedRequest(ServerWebExchange exchange, GatewayFilterChain chain) {
        return nimbusReactiveJwtDecoder.decode(getAuthorizationToken(exchange.getRequest()))
                .flatMap(this::getAuthenticationInfo)
                .flatMap(authenticationInfo -> filterValidatedRequest(chain, exchange, authenticationInfo));
    }

    private Mono<AuthenticationInfo> getAuthenticationInfo(Jwt jwt) {
        return Mono.zip(
                getUsername(jwt),
                getRoles(jwt),
                this::getAuthenticationInfo
        );
    }

    private Mono<String> getUsername(Jwt jwt) {
        return Mono.just(jwtAuthenticationTokenConverter.extractUsername(jwt))
                .switchIfEmpty(Mono.defer(() -> Mono.just(StringUtils.EMPTY)));
    }

    private Mono<List<String>> getRoles(Jwt jwt) {
        return jwtAuthenticationTokenConverter.extractGrantedAuthorities(jwt)
                .map(GrantedAuthority::getAuthority)
                .collectList()
                .switchIfEmpty(Mono.just(List.of()));
    }

    private AuthenticationInfo getAuthenticationInfo(String username, List<String> roles) {
        return AuthenticationInfo.builder()
                .username(username)
                .roles(roles)
                .build();
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

    private Mono<Void> filterValidatedRequest(GatewayFilterChain chain,
                                              ServerWebExchange exchange,
                                              AuthenticationInfo authenticationInfo) {
        return chain.filter(createMutatedServerWebExchange(exchange, authenticationInfo));
    }

    private ServerWebExchange createMutatedServerWebExchange(ServerWebExchange exchange,
                                                             AuthenticationInfo authenticationInfo) {
        return exchange.mutate()
                .request(mutateHeaders(authenticationInfo.username(), authenticationInfo.roles()))
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
