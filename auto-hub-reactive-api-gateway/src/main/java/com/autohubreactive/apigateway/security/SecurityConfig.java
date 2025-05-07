package com.autohubreactive.apigateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ReactiveAuthenticationManager reactiveAuthenticationManager;
    private final ReactiveJwtDecoder reactiveJwtDecoder;

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkUri;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.cors(ServerHttpSecurity.CorsSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(
                        request -> request.pathMatchers(
                                        "/agency/definition/**",
                                        "/ai/definition/**",
                                        "/bookings/definition/**",
                                        "/customers/definition/**",
                                        "/customers/register",
                                        "/expense/definition/**",
                                        "/actuator/**")
                                .permitAll()
                                .anyExchange()
                                .authenticated()
                )
                .exceptionHandling(request ->
                        request.authenticationEntryPoint((response, _) -> getResponse(response, HttpStatus.UNAUTHORIZED))
                                .accessDeniedHandler((response, _) -> getResponse(response, HttpStatus.FORBIDDEN)))
                .oauth2ResourceServer(resourceServerSpec ->
                        resourceServerSpec.jwt(jwtSpec -> jwtSpec.jwkSetUri(jwkUri)
                                .authenticationManager(reactiveAuthenticationManager)
                                .jwtDecoder(reactiveJwtDecoder)))
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .requestCache(request -> request.requestCache(NoOpServerRequestCache.getInstance()))
                .build();
    }

    private Mono<Void> getResponse(ServerWebExchange response, HttpStatus httpStatus) {
        return Mono.fromRunnable(() -> response.getResponse().setStatusCode(httpStatus));
    }

}
