package com.swiftwheelshubreactive.requestvalidator.service;

import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import com.swiftwheelshubreactive.requestvalidator.config.RegisteredEndpoints;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
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

    public Flux<Tuple2<String, String>> getSwaggerIdentifierAndContent() {
        return Flux.fromIterable(registeredEndpoints.getEndpoints().entrySet())
                .flatMap(this::mapIdentifierToSwagger);
    }

    public Mono<String> getSwaggerFileForMicroservice(String microserviceName) {
        return getSwaggerIdentifierAndContent()
                .map(swaggerIdentifierAndContent -> {
                    String microserviceIdentifier = swaggerIdentifierAndContent.getT1();

                    if (microserviceName.contains(microserviceIdentifier)) {
                        return swaggerIdentifierAndContent.getT2();
                    }

                    throw new SwiftWheelsHubException("Microservice " + microserviceName + " not existent");
                })
                .collectList()
                .map(List::getFirst);
    }

    private Mono<Tuple2<String, String>> mapIdentifierToSwagger(Map.Entry<String, String> endpoints) {
        return getSwagger(endpoints.getKey(), endpoints.getValue())
                .map(swaggerContent -> Tuples.of(endpoints.getKey(), swaggerContent));
    }

    private Mono<String> getSwagger(String microserviceName, String url) {
        return webClient.get()
                .uri(url)
                .header(X_API_KEY, apikey)
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(Retry.fixedDelay(6, Duration.ofSeconds(10)))
                .filter(StringUtils::isNotBlank)
                .switchIfEmpty(Mono.error(new SwiftWheelsHubException("Swagger for: " + microserviceName + " is empty")));
    }

}
