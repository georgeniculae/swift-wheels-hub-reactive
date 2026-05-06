package com.autohub.ai.service;

import com.autohub.ai.util.TestUtil;
import com.autohubreactive.ai.service.CarSuggestionService;
import com.autohubreactive.ai.service.CarVectorStoreService;
import com.autohubreactive.ai.service.ChatService;
import com.autohubreactive.dto.ai.CarSuggestionResponse;
import com.autohubreactive.dto.ai.TripInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarSuggestionServiceTest {

    @InjectMocks
    private CarSuggestionService carSuggestionService;

    @Mock
    private CarVectorStoreService carVectorStoreService;

    @Mock
    private ChatService chatService;

    @Test
    void getChatOutputTest_success() {
        TripInfo tripInfo =
                TestUtil.getResourceAsJson("/data/TripInfo.json", TripInfo.class);
        CarSuggestionResponse carSuggestionResponse =
                TestUtil.getResourceAsJson("/data/CarSuggestionResponse.json", CarSuggestionResponse.class);
        String apikey = "apikey";
        Document document = new Document("Toyota RAV4 SUV from 2022, white, 15000 km, price 200 per day");

        when(carVectorStoreService.searchSimilarCars(anyString(), anyInt()))
                .thenReturn(Mono.just(List.of(document)));
        when(chatService.getChatReply(anyString(), anyMap())).thenReturn(Mono.just(carSuggestionResponse));

        carSuggestionService.getChatOutput(apikey, List.of("admin"), tripInfo)
                .as(StepVerifier::create)
                .expectNext(carSuggestionResponse)
                .verifyComplete();
    }

    @Test
    void getChatOutputTest_errorOnVectorSearch() {
        TripInfo tripInfo = TestUtil.getResourceAsJson("/data/TripInfo.json", TripInfo.class);
        String apikey = "apikey";

        when(carVectorStoreService.searchSimilarCars(anyString(), anyInt()))
                .thenReturn(Mono.error(new Throwable()));

        carSuggestionService.getChatOutput(apikey, List.of("admin"), tripInfo)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

}