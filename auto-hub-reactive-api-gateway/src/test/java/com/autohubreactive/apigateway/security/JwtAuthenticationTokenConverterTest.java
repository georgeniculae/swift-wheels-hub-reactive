package com.autohubreactive.apigateway.security;

import com.autohubreactive.apigateway.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationTokenConverterTest {

    @InjectMocks
    private JwtAuthenticationTokenConverter jwtAuthenticationTokenConverter;

    @Mock
    private Converter<Jwt, Flux<GrantedAuthority>> jwtGrantedAuthoritiesConverter;

    @Test
    void extractUsernameTest_success() {
        String token = TestUtil.getResourceAsJson("/data/JwtToken.json", String.class);

        Map<String, Object> headers = Map.of(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        String user = "user";
        Map<String, Object> claims = Map.of("preferred_username", user);

        Jwt jwt = new Jwt(token, Instant.now(), Instant.now().plus(30, ChronoUnit.MINUTES), headers, claims);

        String username = jwtAuthenticationTokenConverter.extractUsername(jwt);
        assertEquals(user, username);
    }

    @Test
    void extractGrantedAuthoritiesTest_success() {
        String token = TestUtil.getResourceAsJson("/data/JwtToken.json", String.class);
        String roleUser = "ROLE_user";
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(roleUser);
        Collection<? extends GrantedAuthority> roles = List.of(simpleGrantedAuthority);

        Map<String, Object> headers = Map.of(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        Map<String, Object> claims = Map.of("realm_access", List.of(roleUser));

        Jwt jwt = new Jwt(token, Instant.now(), Instant.now().plus(30, ChronoUnit.MINUTES), headers, claims);

        when(jwtGrantedAuthoritiesConverter.convert(any(Jwt.class))).thenReturn(Flux.fromIterable(roles));

        Objects.requireNonNull(jwtAuthenticationTokenConverter.extractGrantedAuthorities(jwt))
                .as(StepVerifier::create)
                .expectNext(simpleGrantedAuthority)
                .verifyComplete();
    }

}
