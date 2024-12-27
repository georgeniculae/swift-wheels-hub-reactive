package com.swiftwheelshubreactive.apigateway.security;

import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationTokenConverter {

    private static final String USERNAME_CLAIM = "preferred_username";
    private static final String EMAIL_CLAIM = "email";
    private final Converter<Jwt, Flux<GrantedAuthority>> jwtGrantedAuthoritiesConverter;

    public String extractUsername(Jwt source) {
        return Optional.ofNullable(source.getClaims().get(USERNAME_CLAIM))
                .map(String::valueOf)
                .orElseThrow(() -> new SwiftWheelsHubException("Username claim is null"));
    }

    public String extractEmail(Jwt source) {
        return Optional.ofNullable(source.getClaims().get(EMAIL_CLAIM))
                .map(String::valueOf)
                .orElseThrow(() -> new SwiftWheelsHubException("Email claim is null"));
    }

    public Flux<GrantedAuthority> extractGrantedAuthorities(Jwt source) {
        return jwtGrantedAuthoritiesConverter.convert(source);
    }

}
