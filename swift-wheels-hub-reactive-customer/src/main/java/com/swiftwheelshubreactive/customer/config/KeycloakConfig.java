package com.swiftwheelshubreactive.customer.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Bean
    public Keycloak keycloak(@Value("${keycloak.serverUrl}") String serverUrl,
                             @Value("${keycloak.realm}") String realm,
                             @Value("${keycloak.clientId}") String clientId,
                             @Value("${keycloak.clientSecret}") String clientSecret) {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
    }

}
