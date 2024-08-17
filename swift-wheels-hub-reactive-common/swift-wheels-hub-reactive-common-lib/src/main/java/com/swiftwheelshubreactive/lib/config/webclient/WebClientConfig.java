package com.swiftwheelshubreactive.lib.config.webclient;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean(name = "loadBalancedWebClientBuilder")
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient webClient(@Qualifier("loadBalancedWebClientBuilder") WebClient.Builder webClientBuilder) {
        return webClientBuilder.build();
    }

}
