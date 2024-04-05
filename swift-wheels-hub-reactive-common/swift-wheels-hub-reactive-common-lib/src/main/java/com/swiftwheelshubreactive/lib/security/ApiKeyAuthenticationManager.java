package com.swiftwheelshubreactive.lib.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@ConditionalOnProperty(prefix = "apikey", name = "secret")
public class ApiKeyAuthenticationManager implements ReactiveAuthenticationManager {

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
                .cast(ApiKeyAuthenticationToken.class)
                .map(this::getApiKeyAuthenticationToken);
    }

    private ApiKeyAuthenticationToken getApiKeyAuthenticationToken(ApiKeyAuthenticationToken apiKeyAuthenticationToken) {
        return new ApiKeyAuthenticationToken(
                apiKeyAuthenticationToken.getAuthorities(),
                apiKeyAuthenticationToken.getPrincipal().toString(),
                true
        );
    }

}
