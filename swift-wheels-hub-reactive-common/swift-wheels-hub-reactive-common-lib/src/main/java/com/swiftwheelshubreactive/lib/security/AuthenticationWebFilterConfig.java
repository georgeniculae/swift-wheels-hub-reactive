package com.swiftwheelshubreactive.lib.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;

@Configuration
@RequiredArgsConstructor
@ConditionalOnBean(name = "apiKeySecurityConfig")
public class AuthenticationWebFilterConfig {

    private final ApiKeyAuthenticationManager apiKeyAuthenticationManager;
    private final ApiKeyAuthenticationConverter apiKeyAuthenticationConverter;

    @Bean
    public AuthenticationWebFilter authenticationWebFilter() {
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(apiKeyAuthenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(apiKeyAuthenticationConverter);

        return authenticationWebFilter;
    }

}
