package com.swiftwheelshubreactive.lib.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@ConditionalOnProperty(prefix = "apikey", name = "secret")
public class ApiKeyAuthenticationManager implements ReactiveAuthenticationManager {

    @Value("${apikey.secret}")
    private String apiKeySecret;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
                .filter(auth -> auth instanceof ApiKeyAuthenticationToken && apiKeySecret.equals(auth.getPrincipal().toString()))
                .map(auth -> {
                    ApiKeyAuthenticationToken apiKeyAuthenticationToken = (ApiKeyAuthenticationToken) auth;
                    apiKeyAuthenticationToken.setAuthenticated(true);

                    return apiKeyAuthenticationToken;
                });
    }

}
