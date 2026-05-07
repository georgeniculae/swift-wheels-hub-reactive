package com.autohubreactive.ai.service;

import com.autohubreactive.ai.util.ApiKeyProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CarVectorIndexInitializer {

    private final CarService carService;
    private final CarVectorStoreService carVectorStoreService;
    private final ApiKeyProvider apiKeyProvider;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        carVectorStoreService.deleteAllCars()
                .thenMany(carService.getAllAvailableCars(apiKeyProvider.getApiKey(), apiKeyProvider.getSystemRoles()))
                .flatMap(carVectorStoreService::addCar)
                .doOnComplete(() -> log.info("Vector store reinitialized with all available cars"))
                .doOnError(e -> log.error("Vector store reinitialization failed: {}", e.getMessage()))
                .subscribe();
    }

}
