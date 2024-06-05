package com.swiftwheelshub.ai.service;

import com.swiftwheelshub.ai.util.TestUtils;
import com.swiftwheelshubreactive.dto.CarResponse;
import com.swiftwheelshubreactive.dto.TripInfo;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarSuggestionServiceTest {

    @InjectMocks
    private CarSuggestionService carSuggestionService;

    @Mock
    private CarService carService;

    @Mock
    private ChatLanguageModel chatLanguageModel;

    @Test
    void getChatOutputTest_success() {
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);
        String output = "Test";
        TripInfo tripInfo = TestUtils.getResourceAsJson("/data/TripInfo.json", TripInfo.class);
        String apikey = "apikey";

        when(carService.getAllAvailableCars(anyString(), anyList())).thenReturn(Flux.just(carResponse));
        when(chatLanguageModel.generate(anyString())).thenReturn(output);

        carSuggestionService.getChatOutput(apikey, List.of("admin"), tripInfo)
                .as(StepVerifier::create)
                .expectNext(output)
                .verifyComplete();
    }

    @Test
    void getChatOutputTest_errorOnFindingAvailableCars() {
        TripInfo tripInfo = TestUtils.getResourceAsJson("/data/TripInfo.json", TripInfo.class);
        String apikey = "apikey";

        when(carService.getAllAvailableCars(anyString(), anyList())).thenReturn(Flux.error(new Throwable()));

        carSuggestionService.getChatOutput(apikey, List.of("admin"), tripInfo)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

}
