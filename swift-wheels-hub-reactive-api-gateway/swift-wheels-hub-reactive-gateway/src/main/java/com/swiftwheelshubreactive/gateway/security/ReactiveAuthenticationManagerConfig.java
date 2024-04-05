package com.swiftwheelshubreactive.gateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager;

@Configuration
@RequiredArgsConstructor
public class ReactiveAuthenticationManagerConfig {

    private final NimbusReactiveJwtDecoder nimbusReactiveJwtDecoder;

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
        return new JwtReactiveAuthenticationManager(nimbusReactiveJwtDecoder);
    }

}
