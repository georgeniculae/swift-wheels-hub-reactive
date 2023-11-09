package com.carrental.lib.security.apikey;

import org.apache.commons.lang3.ObjectUtils;
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
        return Mono.fromSupplier(() -> {
            if (authentication != null && ObjectUtils.isNotEmpty(authentication.getCredentials())) {
                authentication.setAuthenticated(true);
            }

            return authentication;
        });
    }

}
