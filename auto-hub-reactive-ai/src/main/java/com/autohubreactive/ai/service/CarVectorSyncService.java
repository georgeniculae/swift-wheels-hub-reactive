package com.autohubreactive.ai.service;

import com.autohubreactive.lib.exceptionhandling.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarVectorSyncService {

    private final CarVectorStoreService carVectorStoreService;

    public Mono<Void> removeCar(String carId) {
        return carVectorStoreService.deleteCar(carId)
                .onErrorMap(e -> {
                    log.error("Error removing car {} from vector store: {}", carId, e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

}