package com.swiftwheelshubreactive.lib.config.webclient;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Bean(name = "loadBalancedWebClientBuilder")
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient webClient(@Qualifier("loadBalancedWebClientBuilder") WebClient.Builder webClientBuilder) {
        HttpClient httpClient = HttpClient.create().responseTimeout(Duration.ofSeconds(20));

        return webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

}
