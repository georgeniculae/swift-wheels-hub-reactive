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

    private final ApiKeyAuthenticationManager apiKeyAuthenticationManager;
    private final ApiKeyAuthenticationConverter apiKeyAuthenticationConverter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.cors(ServerHttpSecurity.CorsSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(request -> request
                        .pathMatchers("/agency/definition/**",
                                "/bookings/definition/**",
                                "/customers/definition/**",
                                "/customers/register",
                                "/expense/definition/**",
                                "/agency/actuator/**",
                                "/bookings/actuator/**",
                                "/customers/actuator/**",
                                "/expense/actuator/**",
                                "/actuator/**").permitAll()
                        .pathMatchers("/agency/**",
                                "/bookings/**",
                                "/customers/**",
                                "/expense/**").authenticated()
                        .anyExchange().authenticated())
                .addFilterAt(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .requestCache(request -> request.requestCache(NoOpServerRequestCache.getInstance()))
                .build();
    }

    private AuthenticationWebFilter authenticationWebFilter() {
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(apiKeyAuthenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(apiKeyAuthenticationConverter);

        return authenticationWebFilter;
    }

}
