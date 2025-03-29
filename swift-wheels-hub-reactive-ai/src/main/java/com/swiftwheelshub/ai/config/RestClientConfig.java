package com.swiftwheelshub.ai.config;

import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient.Builder restClientBuilder() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = ClientHttpRequestFactoryBuilder.httpComponents()
                .withCustomizers(
                        List.of(
                                requestFactory -> requestFactory.setConnectTimeout(Duration.ofSeconds(60)),
                                requestFactory -> requestFactory.setReadTimeout(Duration.ofSeconds(60))
                        )
                )
                .build();

        return RestClient.builder().requestFactory(clientHttpRequestFactory);
    }

    @Bean
    public RestClient restClient(RestClient.Builder restClientBuilder) {
        return restClientBuilder.build();
    }

}
