package com.autohubreactive.apigateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager;

@Configuration
@RequiredArgsConstructor
public class ReactiveAuthenticationManagerConfig {

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(NimbusReactiveJwtDecoder nimbusReactiveJwtDecoder) {
        return new JwtReactiveAuthenticationManager(nimbusReactiveJwtDecoder);
    }

}
