package com.swiftwheelshubreactive.cloudgateway.security;

import com.swiftwheelshubreactive.cloudgateway.util.TestUtils;
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
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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
    void convertTest_success() {
        String token = TestUtils.getResourceAsJson("/data/JwtToken.json", String.class);

        Collection<? extends GrantedAuthority> roles = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        Map<String, Object> headers = Map.of(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        String user = "user";
        Map<String, Object> claims = Map.of("preferred_username", user);

        Jwt jwt = new Jwt(token, Instant.now(), Instant.now().plus(30, ChronoUnit.MINUTES), headers, claims);
        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(jwt, roles, user);

        when(jwtGrantedAuthoritiesConverter.convert(any(Jwt.class))).thenReturn(Flux.fromIterable(roles));

        Objects.requireNonNull(jwtAuthenticationTokenConverter.convert(jwt))
                .as(StepVerifier::create)
                .expectNext(jwtAuthenticationToken)
                .verifyComplete();
    }

    @Test
    void extractUsernameTest_success() {
        String token = TestUtils.getResourceAsJson("/data/JwtToken.json", String.class);

        Map<String, Object> headers = Map.of(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        String user = "user";
        Map<String, Object> claims = Map.of("preferred_username", user);

        Jwt jwt = new Jwt(token, Instant.now(), Instant.now().plus(30, ChronoUnit.MINUTES), headers, claims);

        String username = jwtAuthenticationTokenConverter.extractUsername(jwt);
        assertEquals(user, username);
    }

}
