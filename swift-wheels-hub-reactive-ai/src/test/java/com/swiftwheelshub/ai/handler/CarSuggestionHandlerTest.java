package com.swiftwheelshub.ai.handler;

import com.swiftwheelshub.ai.service.CarSuggestionService;
import com.swiftwheelshub.ai.util.TestUtils;
import com.swiftwheelshub.ai.validator.TripInfoValidator;
import com.swiftwheelshubreactive.dto.TripInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import reactor.core.publisher.Flux;
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
        TripInfo tripInfo = TestUtils.getResourceAsJson("/data/TripInfo.json", TripInfo.class);

        MockServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.POST)
                .header("X-API-KEY", "apikey")
                .header("X-USERNAME", "user")
                .body(Mono.just(tripInfo));

        when(tripInfoValidator.validateBody(any(TripInfo.class))).thenReturn(Mono.just(tripInfo));
        when(carSuggestionService.getChatOutput(anyString(), anyList(), any(TripInfo.class)))
                .thenReturn(Flux.just("Test"));

        StepVerifier.create(carSuggestionHandler.getChatOutput(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

}
