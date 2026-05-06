package com.autohub.ai.service;

import com.autohub.ai.util.TestUtil;
import com.autohubreactive.ai.service.CarVectorStoreService;
import com.autohubreactive.dto.agency.CarResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarVectorStoreServiceTest {

    @InjectMocks
    private CarVectorStoreService carVectorStoreService;

    @Mock
    private VectorStore vectorStore;

    @Test
    void addCarTest_success() {
        CarResponse carResponse =
                TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        doNothing().when(vectorStore).add(anyList());

        carVectorStoreService.addCar(carResponse)
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void addCarTest_error() {
        CarResponse carResponse =
                TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        doThrow(new RuntimeException("Connection error")).when(vectorStore).add(anyList());

        carVectorStoreService.addCar(carResponse)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void deleteCarTest_success() {
        carVectorStoreService.deleteCar("car-id-1")
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void deleteCarTest_error() {
        doThrow(new RuntimeException("Connection error")).when(vectorStore).delete(anyList());

        carVectorStoreService.deleteCar("car-id-1")
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void searchSimilarCarsTest_success() {
        Document document = new Document("Toyota RAV4 SUV from 2022, white, 15000 km, price 200 per day");

        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(document));

        carVectorStoreService.searchSimilarCars("SUV for mountain trip", 10)
                .as(StepVerifier::create)
                .expectNextMatches(docs -> docs.size() == 1)
                .verifyComplete();
    }

    @Test
    void searchSimilarCarsTest_error() {
        when(vectorStore.similaritySearch(any(SearchRequest.class)))
                .thenThrow(new RuntimeException("Connection error"));

        carVectorStoreService.searchSimilarCars("SUV for mountain trip", 10)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

}
