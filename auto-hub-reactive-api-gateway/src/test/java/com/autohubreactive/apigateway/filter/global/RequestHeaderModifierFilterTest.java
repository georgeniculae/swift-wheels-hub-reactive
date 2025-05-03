package com.autohubreactive.apigateway.filter.global;

import com.autohubreactive.apigateway.security.JwtAuthenticationTokenConverter;
import com.autohubreactive.apigateway.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestHeaderModifierFilterTest {

    @InjectMocks
    private RequestHeaderModifierFilter requestHeaderModifierFilter;

    @Mock
    private NimbusReactiveJwtDecoder nimbusReactiveJwtDecoder;

    @Mock
    private JwtAuthenticationTokenConverter jwtAuthenticationTokenConverter;

    @Mock
    private GatewayFilterChain chain;

    @Test
    void filterTest_success() {
        String tokenValue = TestUtil.getResourceAsJson("/data/JwtToken.json", String.class);

        MockServerHttpRequest request = MockServerHttpRequest.get("/agency/rental-offices/{id}", 1)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.builder(request).build();

        Map<String, Object> headers = Map.of(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue);
        Map<String, Object> claims = Map.of("preferred_username", "user");

        Jwt jwt =
                new Jwt(tokenValue, Instant.now(), Instant.now().plus(30, ChronoUnit.MINUTES), headers, claims);
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user");

        when(nimbusReactiveJwtDecoder.decode(anyString())).thenReturn(Mono.just(jwt));
        when(jwtAuthenticationTokenConverter.extractUsername(any(Jwt.class))).thenReturn("user");
        when(jwtAuthenticationTokenConverter.extractEmail(any(Jwt.class))).thenReturn("user@mail.com");
        when(jwtAuthenticationTokenConverter.extractGrantedAuthorities(any(Jwt.class)))
                .thenReturn(Flux.just(simpleGrantedAuthority));
        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        requestHeaderModifierFilter.filter(exchange, chain)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void filterTest_noAuthorizationHeader() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/agency/rental-offices/{id}", 1)
                .accept(MediaType.APPLICATION_JSON)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.builder(request).build();

        requestHeaderModifierFilter.filter(exchange, chain)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void filterTest_notCorrespondingPath() {
        String tokenValue = TestUtil.getResourceAsJson("/data/JwtToken.json", String.class);

        MockServerHttpRequest request = MockServerHttpRequest.get("/agency/swagger-ui.html")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue)
                .build();

        ServerWebExchange exchange = MockServerWebExchange.builder(request).build();

        Map<String, Object> headers = Map.of(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue);
        Map<String, Object> claims = Map.of("preferred_username", "user");

        Jwt jwt = new Jwt(tokenValue, Instant.now(), Instant.now().plus(30, ChronoUnit.MINUTES), headers, claims);

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user");

        when(nimbusReactiveJwtDecoder.decode(anyString())).thenReturn(Mono.just(jwt));
        when(jwtAuthenticationTokenConverter.extractUsername(any(Jwt.class))).thenReturn("user");
        when(jwtAuthenticationTokenConverter.extractEmail(any(Jwt.class))).thenReturn("user@mail.com");
        when(jwtAuthenticationTokenConverter.extractGrantedAuthorities(any(Jwt.class)))
                .thenReturn(Flux.just(simpleGrantedAuthority));
        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        requestHeaderModifierFilter.filter(exchange, chain)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

}
