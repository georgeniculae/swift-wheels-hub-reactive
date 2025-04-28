package com.autohubreactive.apigateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;

@Configuration
public class NimbusReactiveJwtDecoderConfig {

    @Bean
    public NimbusReactiveJwtDecoder nimbusReactiveJwtDecoder(@Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") String jwkUri) {
        return NimbusReactiveJwtDecoder.withJwkSetUri(jwkUri).build();
    }

}
