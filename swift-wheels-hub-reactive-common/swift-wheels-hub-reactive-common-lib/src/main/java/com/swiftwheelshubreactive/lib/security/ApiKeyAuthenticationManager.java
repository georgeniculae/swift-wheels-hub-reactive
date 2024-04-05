package com.swiftwheelshubreactive.lib.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@ConditionalOnBean(name = "apiKeySecurityConfig")
public class ApiKeyAuthenticationManager implements ReactiveAuthenticationManager {

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
                .cast(ApiKeyAuthenticationToken.class)
                .map(apiKeyAuthenticationToken -> {
                    apiKeyAuthenticationToken.setAuthenticated(true);

                    return apiKeyAuthenticationToken;
                });
    }

}
