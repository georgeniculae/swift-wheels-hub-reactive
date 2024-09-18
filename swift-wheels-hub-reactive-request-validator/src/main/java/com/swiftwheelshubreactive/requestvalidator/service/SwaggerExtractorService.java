package com.swiftwheelshubreactive.requestvalidator.service;

import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import com.swiftwheelshubreactive.lib.exceptionhandling.ExceptionUtil;
import com.swiftwheelshubreactive.requestvalidator.config.RegisteredEndpoints;
import com.swiftwheelshubreactive.requestvalidator.model.SwaggerFile;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class SwaggerExtractorService {

    private static final String X_API_KEY = "X-API-KEY";
    private final WebClient webClient;
    private final RegisteredEndpoints registeredEndpoints;

    public Flux<SwaggerFile> getSwaggerFiles() {
        return Flux.fromIterable(registeredEndpoints.getEndpoints())
                .flatMap(this::createSwaggerFile);
    }

    public Mono<SwaggerFile> getSwaggerFileForMicroservice(String microserviceName) {
        return getSwaggerFiles()
                .filter(swaggerFile -> microserviceName.contains(swaggerFile.getIdentifier()))
                .switchIfEmpty(Mono.error(new SwiftWheelsHubException("Microservice " + microserviceName + " not existent")))
                .next();
    }

    private Mono<SwaggerFile> createSwaggerFile(RegisteredEndpoints.RegisteredEndpoint endpoint) {
        return getSwaggerContent(endpoint.getIdentifier(), endpoint.getUrl())
                .map(swaggerContent -> getSwaggerFile(endpoint, swaggerContent));
    }

    private Mono<String> getSwaggerContent(String identifier, String url) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(Retry.fixedDelay(6, Duration.ofSeconds(10)))
                .filter(StringUtils::isNotBlank)
                .switchIfEmpty(Mono.error(new SwiftWheelsHubException("Swagger for: " + identifier + " is empty")))
                .onErrorMap(ExceptionUtil::handleException);
    }

    private SwaggerFile getSwaggerFile(RegisteredEndpoints.RegisteredEndpoint endpoint, String swaggerContent) {
        return SwaggerFile.builder()
                .identifier(endpoint.getIdentifier())
                .swaggerContent(swaggerContent)
                .build();
    }

}
