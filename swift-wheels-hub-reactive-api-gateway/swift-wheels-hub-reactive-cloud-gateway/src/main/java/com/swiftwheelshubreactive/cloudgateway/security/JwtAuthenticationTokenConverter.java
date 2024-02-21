package com.swiftwheelshubreactive.cloudgateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationTokenConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    private static final String USERNAME_CLAIM = "preferred_username";
    private final Converter<Jwt, Flux<GrantedAuthority>> jwtGrantedAuthoritiesConverter;

    @Override
    public Mono<AbstractAuthenticationToken> convert(@NonNull Jwt source) {
        return extractGrantedAuthorities(source)
                .collectList()
                .map(authorities -> new JwtAuthenticationToken(source, authorities, extractUsername(source)));
    }

    public String extractUsername(Jwt source) {
        return (String) source.getClaims().get(USERNAME_CLAIM);
    }

    public Flux<GrantedAuthority> extractGrantedAuthorities(Jwt source) {
        return jwtGrantedAuthoritiesConverter.convert(source);
    }

}
