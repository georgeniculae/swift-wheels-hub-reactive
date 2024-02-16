package com.swiftwheelshub.cloudgateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final NimbusReactiveJwtDecoder nimbusReactiveJwtDecoder;
    private final JwtAuthenticationTokenConverter jwtAuthenticationTokenConverter;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
                .map(this::getAuthorization)
                .flatMap(nimbusReactiveJwtDecoder::decode)
                .flatMap(this::getJwtAuthenticationToken);
    }

    private String getAuthorization(Authentication authentication) {
        return authentication.getPrincipal().toString();
    }

    private Mono<AbstractAuthenticationToken> getJwtAuthenticationToken(Jwt jwt) {
        return jwtAuthenticationTokenConverter.convert(jwt);
    }

}
