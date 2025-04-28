package com.autohub.ai.service;

import com.autohub.ai.util.TestUtil;
import com.autohubreactive.dto.CarResponse;
import com.autohubreactive.dto.CarSuggestionResponse;
import com.autohubreactive.dto.TripInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarSuggestionServiceTest {

    @InjectMocks
    private CarSuggestionService carSuggestionService;

    @Mock
    private CarService carService;

    @Mock
    private ChatService chatService;

    @Test
    void getChatOutputTest_success() {
        CarResponse carResponse =
                TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);
        TripInfo tripInfo =
                TestUtil.getResourceAsJson("/data/TripInfo.json", TripInfo.class);
        CarSuggestionResponse carSuggestionResponse =
                TestUtil.getResourceAsJson("/data/CarSuggestionResponse.json", CarSuggestionResponse.class);
        String apikey = "apikey";

        when(carService.getAllAvailableCars(anyString(), anyList())).thenReturn(Flux.just(carResponse));
        when(chatService.getChatReply(anyString(), anyMap())).thenReturn(Mono.just(carSuggestionResponse));

        carSuggestionService.getChatOutput(apikey, List.of("admin"), tripInfo)
                .as(StepVerifier::create)
                .expectNext(carSuggestionResponse)
                .verifyComplete();
    }

    @Test
    void getChatOutputTest_errorOnFindingAvailableCars() {
        TripInfo tripInfo = TestUtil.getResourceAsJson("/data/TripInfo.json", TripInfo.class);
        String apikey = "apikey";

        when(carService.getAllAvailableCars(anyString(), anyList())).thenReturn(Flux.error(new Throwable()));

        carSuggestionService.getChatOutput(apikey, List.of("admin"), tripInfo)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

}
