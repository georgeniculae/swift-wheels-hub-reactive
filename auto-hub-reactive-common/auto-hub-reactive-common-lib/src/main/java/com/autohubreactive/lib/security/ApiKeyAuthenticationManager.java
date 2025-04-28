package com.autohubreactive.lib.security;

import com.autohubreactive.exception.AutoHubResponseStatusException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
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
        return Mono.just(authentication)
                .filter(this::isValidAuthentication)
                .doOnNext(auth -> auth.setAuthenticated(true))
                .switchIfEmpty(
                        Mono.error(
                                new AutoHubResponseStatusException(
                                        HttpStatus.UNAUTHORIZED,
                                        "No matching API Key"
                                )
                        )
                );
    }

    private boolean isValidAuthentication(Authentication authentication) {
        return authentication instanceof ApiKeyAuthenticationToken apiKeyAuthenticationToken &&
                apikeySecret.equals(apiKeyAuthenticationToken.getPrincipal().toString());
    }

}
