package com.swiftwheelshubreactive.cloudgateway.filter.global;

import com.swiftwheelshubreactive.cloudgateway.security.JwtAuthenticationTokenConverter;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubResponseStatusException;
import lombok.RequiredArgsConstructor;
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
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
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
                .flatMap(chain::filter);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private Mono<ServerWebExchange> modifyHeaders(ServerWebExchange exchange) {
        return getUsername(exchange.getRequest())
                .zipWith(getRoles(exchange.getRequest()))
                .map(usernameAndAuthorities -> mutateServerWebExchange(exchange, usernameAndAuthorities));
    }

    private Mono<String> getUsername(ServerHttpRequest request) {
        return Mono.just(request)
                .filter(this::doesPathContainPattern)
                .flatMap(serverHttpRequest -> nimbusReactiveJwtDecoder.decode(getAuthorizationToken(request)))
                .map(jwtAuthenticationTokenConverter::extractUsername)
                .switchIfEmpty(Mono.defer(() -> Mono.just(StringUtils.EMPTY)));
    }

    private Mono<List<String>> getRoles(ServerHttpRequest request) {
        return Mono.just(request)
                .filter(this::doesPathContainPattern)
                .flatMap(serverHttpRequest -> nimbusReactiveJwtDecoder.decode(getAuthorizationToken(request)))
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
                                                      Tuple2<String, List<String>> usernameAndAuthorities) {
        return exchange.mutate()
                .request(mutateHeaders(usernameAndAuthorities))
                .build();
    }

    private Consumer<ServerHttpRequest.Builder> mutateHeaders(Tuple2<String, List<String>> usernameAndAuthorities) {
        return requestBuilder -> {
            requestBuilder.header(X_API_KEY_HEADER, apikey);

            String username = usernameAndAuthorities.getT1();
            if (ObjectUtils.isNotEmpty(username)) {
                requestBuilder.header(X_USERNAME, username);
            }

            List<String> roles = usernameAndAuthorities.getT2();
            if (ObjectUtils.isNotEmpty(roles)) {
                requestBuilder.header(X_ROLES, roles.toArray(new String[]{}));
            }

            requestBuilder.headers(headers -> headers.remove(HttpHeaders.AUTHORIZATION));
        };
    }

}
