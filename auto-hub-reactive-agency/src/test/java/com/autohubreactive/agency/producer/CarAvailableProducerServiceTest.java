package com.autohubreactive.agency.producer;

import com.autohubreactive.agency.util.TestUtil;
import com.autohubreactive.dto.ai.AvailableCarDetails;
import com.autohubreactive.lib.retry.RetryHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.MimeType;
import reactor.test.StepVerifier;
import reactor.util.retry.RetrySpec;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarAvailableProducerServiceTest {

    @InjectMocks
    private CarAvailableProducerService carAvailableProducerService;

    @Mock
    private StreamBridge streamBridge;

    @Mock
    private RetryHandler retryHandler;

    @Test
    void sendCarAvailableTest_success() {
        AvailableCarDetails availableCarDetails =
                TestUtil.getResourceAsJson("/data/AvailableCarDetails.json", AvailableCarDetails.class);

        ReflectionTestUtils.setField(carAvailableProducerService, "binderName", "car-available-ai-topic");
        ReflectionTestUtils.setField(carAvailableProducerService, "mimeType", "application/json");

        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofMinutes(0)));
        when(streamBridge.send(anyString(), any(Message.class), any(MimeType.class))).thenReturn(true);

        carAvailableProducerService.sendCarAvailable(availableCarDetails)
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void sendCarAvailableTest_errorOnSend() {
        AvailableCarDetails availableCarDetails =
                TestUtil.getResourceAsJson("/data/AvailableCarDetails.json", AvailableCarDetails.class);

        ReflectionTestUtils.setField(carAvailableProducerService, "binderName", "car-available-ai-topic");
        ReflectionTestUtils.setField(carAvailableProducerService, "mimeType", "application/json");

        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofMinutes(0)));
        when(streamBridge.send(anyString(), any(Message.class), any(MimeType.class))).thenThrow(new RuntimeException("Kafka unavailable"));

        carAvailableProducerService.sendCarAvailable(availableCarDetails)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

}
