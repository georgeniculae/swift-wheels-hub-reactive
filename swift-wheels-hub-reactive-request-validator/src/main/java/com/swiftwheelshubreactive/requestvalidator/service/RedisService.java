package com.swiftwheelshubreactive.requestvalidator.service;

import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import com.swiftwheelshubreactive.requestvalidator.model.SwaggerFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private final ReactiveRedisOperations<String, SwaggerFile> reactiveRedisOperations;
    private final SwaggerExtractorService swaggerExtractorService;

    public Mono<Boolean> addSwaggerFilesToRedis() {
        return swaggerExtractorService.getSwaggerIdentifierAndContent()
                .flatMapMany(swaggerIdentifierAndContent -> Flux.fromIterable(swaggerIdentifierAndContent.entrySet()))
                .flatMap(swaggerEntry -> addSwaggerToRedis(swaggerEntry.getKey(), swaggerEntry.getValue()))
                .filter(Boolean.TRUE::equals)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new SwiftWheelsHubException("Redis add failed"))))
                .collectList()
                .map(List::getFirst)
                .onErrorResume(e -> {
                    log.error("Error while setting swagger folder in Redis: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e));
                });
    }

    public Mono<Boolean> repopulateRedisWithSwaggerFiles(String microserviceName) {
        return reactiveRedisOperations.delete(microserviceName)
                .flatMap(numberOfDeletedItems -> swaggerExtractorService.getSwaggerFileForMicroservice(microserviceName))
                .flatMap(swaggerContent -> addSwaggerToRedis(microserviceName, swaggerContent))
                .onErrorResume(e -> {
                    log.error("Error while repopulating swagger folder in Redis: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e));
                });
    }

    private Mono<Boolean> addSwaggerToRedis(String key, String value) {
        return reactiveRedisOperations.opsForValue().set(
                key,
                SwaggerFile.builder()
                        .id(key)
                        .swaggerContent(value)
                        .build()
        );
    }

}