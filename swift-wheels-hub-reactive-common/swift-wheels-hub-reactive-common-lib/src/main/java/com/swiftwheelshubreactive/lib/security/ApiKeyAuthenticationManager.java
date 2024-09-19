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
    private String apikeySecret;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
                .filter(auth -> apikeySecret.equals(auth.getPrincipal().toString()))
                .doOnNext(auth -> auth.setAuthenticated(true));
    }

}
