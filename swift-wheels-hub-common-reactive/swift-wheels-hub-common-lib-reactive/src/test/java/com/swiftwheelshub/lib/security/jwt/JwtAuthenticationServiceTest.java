package com.swiftwheelshub.lib.security.jwt;

import com.swiftwheelshub.dto.AuthenticationRequest;
import com.swiftwheelshub.lib.util.TestUtils;
import com.swiftwheelshub.model.User;
import com.carrental.dto.AuthenticationResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationServiceTest {

    @InjectMocks
    private JwtAuthenticationService jwtAuthenticationService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void authenticateTest_success() {
        AuthenticationRequest authenticationRequest =
                TestUtils.getResourceAsJson("/data/AuthenticationRequest.json", AuthenticationRequest.class);

        User user = TestUtils.getResourceAsJson("/data/User.json", User.class);

        String token = "token";
        AuthenticationResponse authenticationResponse = new AuthenticationResponse().token(token);

        when(userDetailsService.findByUsername(anyString())).thenReturn(Mono.just(user));
        when(passwordEncoder.matches(any(CharSequence.class), anyString())).thenReturn(true);
        when(jwtService.generateToken(any())).thenReturn(token);

        StepVerifier.create(jwtAuthenticationService.authenticateUser(authenticationRequest))
                .expectNextMatches(actualResponse -> Objects.equals(authenticationResponse.getToken(), actualResponse.getToken()))
                .verifyComplete();
    }

    @Test
    void authenticateTest_throwExceptionOnFindingByUsername() {
        AuthenticationRequest authenticationRequest =
                TestUtils.getResourceAsJson("/data/AuthenticationRequest.json", AuthenticationRequest.class);

        when(userDetailsService.findByUsername(anyString())).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(jwtAuthenticationService.authenticateUser(authenticationRequest))
                .expectComplete()
                .verify();
    }

}
