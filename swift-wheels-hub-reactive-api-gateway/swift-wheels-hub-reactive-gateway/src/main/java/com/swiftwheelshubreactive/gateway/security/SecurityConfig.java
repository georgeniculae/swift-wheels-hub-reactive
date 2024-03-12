package com.swiftwheelshubreactive.gateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkUri;
    private final AuthenticationManager reactiveAuthenticationManager;
    private final JwtAuthenticationTokenConverter jwtAuthenticationTokenConverter;
    private final ReactiveSecurityContextRepository reactiveSecurityContextRepository;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.cors(ServerHttpSecurity.CorsSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(request ->
                        request.pathMatchers("/agency/definition/**",
                                        "/bookings/definition/**",
                                        "/customers/definition/**",
                                        "/customers/register",
                                        "/expense/definition/**",
                                        "/actuator/**").permitAll()
                                .pathMatchers("/agency/**",
                                        "/bookings/**",
                                        "/customers/**",
                                        "/expense/**").authenticated()
                                .anyExchange().authenticated())
                .exceptionHandling(request ->
                        request.authenticationEntryPoint((response, error) -> getResponse(response, HttpStatus.UNAUTHORIZED))
                                .accessDeniedHandler((response, error) -> getResponse(response, HttpStatus.FORBIDDEN)))
                .oauth2ResourceServer(resourceServerSpec ->
                        resourceServerSpec.jwt(jwtSpec -> jwtSpec.jwkSetUri(jwkUri)
                                .authenticationManager(reactiveAuthenticationManager)
                                .jwtAuthenticationConverter(jwtAuthenticationTokenConverter)))
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .securityContextRepository(reactiveSecurityContextRepository)
                .requestCache(request -> request.requestCache(NoOpServerRequestCache.getInstance()))
                .build();
    }

    private Mono<Void> getResponse(ServerWebExchange response, HttpStatus httpStatus) {
        return Mono.fromRunnable(() -> response.getResponse().setStatusCode(httpStatus));
    }

}
