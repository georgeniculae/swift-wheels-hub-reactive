package com.autohubreactive.requestvalidator.service;

import com.autohubreactive.exception.AutoHubException;
import com.autohubreactive.lib.exceptionhandling.ExceptionUtil;
import com.autohubreactive.requestvalidator.model.SwaggerFile;
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
                .switchIfEmpty(Mono.error(new AutoHubException("Redis add failed")))
                .onErrorMap(e -> {
                    log.error("Error while setting swagger folder in Redis: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    public Mono<Boolean> repopulateRedisWithSwaggerFiles(String microserviceName) {
        return reactiveRedisOperations.delete(microserviceName)
                .flatMap(_ -> swaggerExtractorService.getSwaggerFileForMicroservice(microserviceName))
                .flatMap(this::addSwaggerToRedis)
                .onErrorMap(e -> {
                    log.error("Error while repopulating swagger folder in Redis: {}", e.getMessage());

                    return new AutoHubException(e.getMessage());
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
