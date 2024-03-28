package com.swiftwheelshubreactive.lib.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "apikey", name = "secret")
public class ApiKeySecurityConfig {

    private final AuthenticationWebFilter authenticationWebFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(request -> request
                        .pathMatchers("/agency/definition/**",
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
                .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .requestCache(request -> request.requestCache(NoOpServerRequestCache.getInstance()))
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .build();
    }

}
