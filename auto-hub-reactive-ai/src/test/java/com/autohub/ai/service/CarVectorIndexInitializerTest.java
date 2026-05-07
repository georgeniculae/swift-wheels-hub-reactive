package com.autohub.ai.service;

import com.autohub.ai.util.TestUtil;
import com.autohubreactive.ai.service.CarService;
import com.autohubreactive.ai.service.CarVectorIndexInitializer;
import com.autohubreactive.ai.service.CarVectorStoreService;
import com.autohubreactive.ai.util.ApiKeyProvider;
import com.autohubreactive.dto.ai.AvailableCarDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarVectorIndexInitializerTest {

    @InjectMocks
    private CarVectorIndexInitializer carVectorIndexInitializer;

    @Mock
    private CarService carService;

    @Mock
    private CarVectorStoreService carVectorStoreService;

    @Mock
    private ApiKeyProvider apiKeyProvider;

    @Test
    void onApplicationReady_success() {
        AvailableCarDetails car =
                TestUtil.getResourceAsJson("/data/AvailableCarDetails.json", AvailableCarDetails.class);

        when(apiKeyProvider.getApiKey()).thenReturn("test-api-key");
        when(apiKeyProvider.getSystemRoles()).thenReturn(List.of("ROLE_admin"));
        when(carVectorStoreService.deleteAllCars()).thenReturn(Mono.empty());
        when(carService.getAllAvailableCars(anyString(), anyList())).thenReturn(Flux.just(car));
        when(carVectorStoreService.addCar(any())).thenReturn(Mono.empty());

        carVectorIndexInitializer.onApplicationReady();

        verify(carVectorStoreService).deleteAllCars();
        verify(carVectorStoreService).addCar(car);
    }

    @Test
    void onApplicationReady_deleteAllCarsFails_addCarNotCalled() {
        when(apiKeyProvider.getApiKey()).thenReturn("test-api-key");
        when(apiKeyProvider.getSystemRoles()).thenReturn(List.of("ROLE_admin"));
        when(carVectorStoreService.deleteAllCars())
                .thenReturn(Mono.error(new RuntimeException("Clear failed")));
        when(carService.getAllAvailableCars(anyString(), anyList())).thenReturn(Flux.empty());

        carVectorIndexInitializer.onApplicationReady();

        verify(carVectorStoreService, never()).addCar(any());
    }

}