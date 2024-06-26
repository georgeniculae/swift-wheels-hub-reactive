package com.swiftwheelshubreactive.lib.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "apikey", name = "secret")
public class ApiKeySecurityConfig {

    private final LoadSecurityContextRepository loadSecurityContextRepository;
    private final ReactiveAuthenticationManager reactiveAuthenticationManager;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(request -> request.pathMatchers(
                                "/agency/definition/**",
                                "/ai/definition/**",
                                "/bookings/definition/**",
                                "/customers/definition/**",
                                "/customers/register",
                                "/expense/definition/**",
                                "/actuator/**"
                        )
                        .permitAll()
                        .pathMatchers(
                                "/agency/**",
                                "/ai/**",
                                "/bookings/**",
                                "/customers/**",
                                "/expense/**"
                        )
                        .authenticated()
                        .anyExchange().authenticated())
                .securityContextRepository(loadSecurityContextRepository)
                .authenticationManager(reactiveAuthenticationManager)
                .requestCache(request -> request.requestCache(NoOpServerRequestCache.getInstance()))
                .build();
    }

}
