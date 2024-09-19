package com.swiftwheelshubreactive.lib.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.test.StepVerifier;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class ApiKeyAuthenticationManagerTest {

    @InjectMocks
    private ApiKeyAuthenticationManager apiKeyAuthenticationManager;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(apiKeyAuthenticationManager, "apikeySecret", "apikey");
    }

    @Test
    void authenticateTest_success() {
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("role");

        ApiKeyAuthenticationToken apiKeyAuthenticationToken = new ApiKeyAuthenticationToken(
                List.of(simpleGrantedAuthority),
                "apikey"
        );

        apiKeyAuthenticationManager.authenticate(apiKeyAuthenticationToken)
                .as(StepVerifier::create)
                .expectNextMatches(Authentication::isAuthenticated)
                .verifyComplete();
    }

    @Test
    void authenticateTest_notMatchingApiKey() {
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("role");

        ApiKeyAuthenticationToken apiKeyAuthenticationToken = new ApiKeyAuthenticationToken(
                List.of(simpleGrantedAuthority),
                "test"
        );

        apiKeyAuthenticationManager.authenticate(apiKeyAuthenticationToken)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

}
