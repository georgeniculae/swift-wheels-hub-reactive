package com.swiftwheelshubreactive.gateway.security;

import com.swiftwheelshubreactive.gateway.util.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoadSecurityContextRepositoryTest {

    @InjectMocks
    private LoadSecurityContextRepository loadSecurityContextRepository;

    @Mock
    private ReactiveAuthenticationManager reactiveAuthenticationManager;

    @Test
    void loadTest_success() {
        String token = TestUtils.getResourceAsJson("/data/JwtToken.json", String.class);

        String username = "user";
        Collection<? extends GrantedAuthority> roles = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        Map<String, Object> headers = Map.of(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        Map<String, Object> claims = Map.of("preferred_username", "user");

        Jwt jwt = new Jwt(token, Instant.now(), Instant.now().plus(30, ChronoUnit.MINUTES), headers, claims);
        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(jwt, roles, username);

        MockServerHttpRequest request = MockServerHttpRequest.get("/agency/rental-offices/{id}", 1)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.builder(request).build();

        SecurityContextImpl securityContext = new SecurityContextImpl(jwtAuthenticationToken);

        when(reactiveAuthenticationManager.authenticate(any(Authentication.class))).thenReturn(Mono.just(jwtAuthenticationToken));

        loadSecurityContextRepository.load(exchange)
                .as(StepVerifier::create)
                .expectNext(securityContext)
                .verifyComplete();
    }

}
