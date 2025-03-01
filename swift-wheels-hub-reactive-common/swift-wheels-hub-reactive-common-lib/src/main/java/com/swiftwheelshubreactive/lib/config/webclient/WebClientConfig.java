package com.swiftwheelshubreactive.lib.config.webclient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
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
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60000)
                .responseTimeout(Duration.ofSeconds(60))
                .doOnConnected(
                        connection -> connection
                                .addHandlerLast(new ReadTimeoutHandler(60))
                                .addHandlerLast(new WriteTimeoutHandler(60))
                );

        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient));
    }

    @Bean
    public WebClient webClient(@Qualifier("loadBalancedWebClientBuilder") WebClient.Builder webClientBuilder) {
        return webClientBuilder.build();
    }

}
