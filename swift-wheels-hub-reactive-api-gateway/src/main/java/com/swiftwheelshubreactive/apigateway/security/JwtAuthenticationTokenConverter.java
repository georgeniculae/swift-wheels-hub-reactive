package com.swiftwheelshubreactive.apigateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationTokenConverter {

    private static final String USERNAME_CLAIM = "preferred_username";
    private final Converter<Jwt, Flux<GrantedAuthority>> jwtGrantedAuthoritiesConverter;

    public String extractUsername(Jwt source) {
        return (String) source.getClaims().get(USERNAME_CLAIM);
    }

    public Flux<GrantedAuthority> extractGrantedAuthorities(Jwt source) {
        return jwtGrantedAuthoritiesConverter.convert(source);
    }

}
