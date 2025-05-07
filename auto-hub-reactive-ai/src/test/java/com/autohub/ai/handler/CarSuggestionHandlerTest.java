package com.autohub.ai.handler;

import com.autohub.ai.service.CarSuggestionService;
import com.autohub.ai.util.TestUtil;
import com.autohub.ai.validator.TripInfoValidator;
import com.autohubreactive.dto.ai.CarSuggestionResponse;
import com.autohubreactive.dto.ai.TripInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarSuggestionHandlerTest {

    @InjectMocks
    private CarSuggestionHandler carSuggestionHandler;

    @Mock
    private CarSuggestionService carSuggestionService;

    @Mock
    private TripInfoValidator tripInfoValidator;

    @Test
    void getChatOutputTest_success() {
        TripInfo tripInfo =
                TestUtil.getResourceAsJson("/data/TripInfo.json", TripInfo.class);
        CarSuggestionResponse carSuggestionResponse =
                TestUtil.getResourceAsJson("/data/CarSuggestionResponse.json", CarSuggestionResponse.class);

        MockServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.POST)
                .header("X-API-KEY", "apikey")
                .header("X-USERNAME", "user")
                .queryParam("destination", "Sinaia")
                .queryParam("peopleCount", "3")
                .queryParam("tripKind", "city")
                .queryParam("tripDate", "2024-06-20")
                .build();

        when(tripInfoValidator.validateBody(any(TripInfo.class))).thenReturn(Mono.just(tripInfo));
        when(carSuggestionService.getChatOutput(anyString(), anyList(), any(TripInfo.class)))
                .thenReturn(Mono.just(carSuggestionResponse));

        carSuggestionHandler.getChatOutput(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

}
