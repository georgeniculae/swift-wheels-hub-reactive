package com.autohub.ai.service;

import com.autohubreactive.ai.service.CarVectorStoreService;
import com.autohubreactive.ai.service.CarVectorSyncService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarVectorSyncServiceTest {

    @InjectMocks
    private CarVectorSyncService carVectorSyncService;

    @Mock
    private CarVectorStoreService carVectorStoreService;

    @Test
    void removeCarTest_success() {
        when(carVectorStoreService.deleteCar(anyString())).thenReturn(Mono.empty());

        carVectorSyncService.removeCar("car-id-1")
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void removeCarTest_error() {
        when(carVectorStoreService.deleteCar(anyString()))
                .thenReturn(Mono.error(new RuntimeException("Connection error")));

        carVectorSyncService.removeCar("car-id-1")
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

}