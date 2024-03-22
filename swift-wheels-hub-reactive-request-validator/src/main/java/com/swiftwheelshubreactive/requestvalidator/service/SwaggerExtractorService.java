package com.swiftwheelshubreactive.requestvalidator.service;

import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import com.swiftwheelshubreactive.requestvalidator.config.RegisteredEndpoints;
import com.swiftwheelshubreactive.requestvalidator.model.SwaggerFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SwaggerExtractorService {

    private static final String X_API_KEY = "X-API-KEY";

    @Value("${apikey.secret}")
    private String apikey;

    private final WebClient webClient;

    private final RegisteredEndpoints registeredEndpoints;

    public Flux<SwaggerFile> getSwaggerFiles() {
        return Flux.fromIterable(registeredEndpoints.getEndpoints().entrySet())
                .flatMap(this::createSwaggerFile);
    }

    public Mono<SwaggerFile> getSwaggerFileForMicroservice(String microserviceName) {
        return getSwaggerFiles()
                .map(swaggerFile -> {
                    String identifier = swaggerFile.getIdentifier();

                    if (microserviceName.contains(identifier)) {
                        return swaggerFile;
                    }

                    throw new SwiftWheelsHubException("Microservice " + microserviceName + " not existent");
                })
                .collectList()
                .map(List::getFirst);
    }

    private Mono<SwaggerFile> createSwaggerFile(Map.Entry<String, String> endpoints) {
        return getSwaggerContent(endpoints.getKey(), endpoints.getValue())
                .map(swaggerContent -> getSwaggerFile(endpoints, swaggerContent));
    }

    private Mono<String> getSwaggerContent(String microserviceName, String url) {
        return webClient.get()
                .uri(url)
                .header(X_API_KEY, apikey)
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(Retry.fixedDelay(6, Duration.ofSeconds(10)))
                .filter(StringUtils::isNotBlank)
                .switchIfEmpty(Mono.error(new SwiftWheelsHubException("Swagger for: " + microserviceName + " is empty")));
    }

    private SwaggerFile getSwaggerFile(Map.Entry<String, String> endpoints, String swaggerContent) {
        return SwaggerFile.builder()
                .identifier(endpoints.getKey())
                .swaggerContent(swaggerContent)
                .build();
    }

}
