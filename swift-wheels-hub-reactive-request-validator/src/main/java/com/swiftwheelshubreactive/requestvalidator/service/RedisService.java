package com.swiftwheelshubreactive.requestvalidator.service;

import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import com.swiftwheelshubreactive.requestvalidator.model.SwaggerFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private final ReactiveRedisOperations<String, SwaggerFile> reactiveRedisOperations;
    private final SwaggerExtractorService swaggerExtractorService;

    public Mono<Boolean> addSwaggerFilesToRedis() {
        return swaggerExtractorService.getSwaggerFiles()
                .collectMap(SwaggerFile::getIdentifier)
                .flatMap(this::addSwaggersToRedis)
                .filter(Boolean.TRUE::equals)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new SwiftWheelsHubException("Redis add failed"))))
                .onErrorMap(e -> {
                    log.error("Error while setting swagger folder in Redis: {}", e.getMessage());

                    return new SwiftWheelsHubException(e);
                });
    }

    public Mono<Boolean> repopulateRedisWithSwaggerFiles(String microserviceName) {
        return reactiveRedisOperations.delete(microserviceName)
                .then(Mono.defer(() -> swaggerExtractorService.getSwaggerFileForMicroservice(microserviceName)))
                .flatMap(this::addSwaggerToRedis)
                .onErrorMap(e -> {
                    log.error("Error while repopulating swagger folder in Redis: {}", e.getMessage());

                    return new SwiftWheelsHubException(e);
                });
    }

    private Mono<Boolean> addSwaggersToRedis(Map<String, SwaggerFile> swaggerFilesMap) {
        return reactiveRedisOperations.opsForValue()
                .multiSet(swaggerFilesMap);
    }

    private Mono<Boolean> addSwaggerToRedis(SwaggerFile swaggerFile) {
        return reactiveRedisOperations.opsForValue()
                .set(swaggerFile.getIdentifier(), swaggerFile);
    }

}
