package com.swiftwheelshubreactive.cloudgateway.security;

import com.swiftwheelshubreactive.cloudgateway.util.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationManagerTest {

    @InjectMocks
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @Mock
    private NimbusReactiveJwtDecoder nimbusReactiveJwtDecoder;

    @Mock
    private JwtAuthenticationTokenConverter jwtAuthenticationTokenConverter;

    @Test
    void authenticateTest_success() {
        String token = TestUtils.getResourceAsJson("/data/JwtToken.json", String.class);

        String username = "user";
        Collection<? extends GrantedAuthority> roles = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        Map<String, Object> headers = Map.of(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        Map<String, Object> claims = Map.of("preferred_username", "user");

        Jwt jwt = new Jwt(token, Instant.now(), Instant.now().plus(30, ChronoUnit.MINUTES), headers, claims);
        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(jwt, roles, username);

        when(authentication.getPrincipal()).thenReturn(token);
        when(nimbusReactiveJwtDecoder.decode(anyString())).thenReturn(Mono.just(jwt));
        when(jwtAuthenticationTokenConverter.convert(jwt)).thenReturn(Mono.just(jwtAuthenticationToken));

        authenticationManager.authenticate(authentication)
                .as(StepVerifier::create)
                .expectNextMatches(auth -> auth.isAuthenticated() && roles.equals(auth.getAuthorities()))
                .verifyComplete();
    }

}
