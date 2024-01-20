package com.swiftwheelshub.lib.security.apikey;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnBean(name = "apiKeySecurityConfig")
public class AuthenticationWebFilterConfig {

    private final ApiKeyAuthenticationManager apiKeyAuthenticationManager;
    private final ApiKeyAuthenticationConverter apiKeyAuthenticationConverter;

    @Bean
    public AuthenticationWebFilter authenticationWebFilter() {
        final AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(apiKeyAuthenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(apiKeyAuthenticationConverter);

        return authenticationWebFilter;
    }

}
