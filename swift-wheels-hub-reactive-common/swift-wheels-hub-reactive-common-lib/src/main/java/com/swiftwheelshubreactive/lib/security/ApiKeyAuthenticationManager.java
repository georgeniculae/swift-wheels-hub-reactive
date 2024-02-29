package com.swiftwheelshubreactive.lib.security;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@ConditionalOnBean(name = "apiKeySecurityConfig")
public class ApiKeyAuthenticationManager implements ReactiveAuthenticationManager {

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
                .map(auth -> getApiKeyAuthenticationToken(authentication));
    }

    private ApiKeyAuthenticationToken getApiKeyAuthenticationToken(Authentication authentication) {
        return new ApiKeyAuthenticationToken(getRoles(authentication), authentication.getPrincipal().toString(), true);
    }

    private List<SimpleGrantedAuthority> getRoles(Authentication authentication) {
        return authentication.getAuthorities()
                .stream()
                .filter(ObjectUtils::isNotEmpty)
                .map(grantedAuthority -> new SimpleGrantedAuthority(grantedAuthority.getAuthority()))
                .toList();
    }

}
