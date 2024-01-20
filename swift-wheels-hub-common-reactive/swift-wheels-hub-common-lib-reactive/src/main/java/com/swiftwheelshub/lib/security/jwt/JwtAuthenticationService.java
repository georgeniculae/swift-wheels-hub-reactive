package com.swiftwheelshub.lib.security.jwt;

import com.swiftwheelshub.dto.AuthenticationRequest;
import com.carrental.dto.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@ConditionalOnBean(name = "jwtSecurityConfig")
@Slf4j
public class JwtAuthenticationService {

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public Mono<AuthenticationResponse> authenticateUser(AuthenticationRequest authenticationRequest) {
        return userDetailsService.findByUsername(authenticationRequest.username())
                .filter(existingUser -> doPasswordsMatch(authenticationRequest, existingUser))
                .map(user -> new AuthenticationResponse().token(jwtService.generateToken(user)))
                .onErrorResume(e -> {
                    log.error("Error while processing request: {}", e.getMessage());

                    return Mono.empty();
                });
    }

    private boolean doPasswordsMatch(AuthenticationRequest authenticationRequest, UserDetails existingUser) {
        return passwordEncoder.matches(authenticationRequest.password(), existingUser.getPassword());
    }

}
