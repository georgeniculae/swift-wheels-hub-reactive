package com.autohub.ai.consumer;

import com.autohub.ai.util.TestUtil;
import com.autohubreactive.ai.consumer.UpdateCarsMessageConsumer;
import com.autohubreactive.ai.service.CarVectorSyncService;
import com.autohubreactive.dto.common.UpdateCarsRequest;
import com.autohubreactive.lib.retry.RetryHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.retry.RetrySpec;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateCarsMessageConsumerTest {

    @InjectMocks
    private UpdateCarsMessageConsumer updateCarsMessageConsumer;

    @Mock
    private CarVectorSyncService carVectorSyncService;

    @Mock
    private Acknowledgment acknowledgment;

    @Mock
    private RetryHandler retryHandler;

    @Test
    void updateCarsConsumerTest_success() {
        UpdateCarsRequest updateCarsRequest =
                TestUtil.getResourceAsJson("/data/UpdateCarsRequest.json", UpdateCarsRequest.class);

        Message<UpdateCarsRequest> message = MessageBuilder.withPayload(updateCarsRequest)
                .setHeader(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment)
                .build();

        when(carVectorSyncService.removeCar(anyString())).thenReturn(Mono.empty());
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofMinutes(0)));

        updateCarsMessageConsumer.updateCarsConsumer().apply(Flux.just(message))
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void updateCarsConsumerTest_noAcknowledgement() {
        UpdateCarsRequest updateCarsRequest =
                TestUtil.getResourceAsJson("/data/UpdateCarsRequest.json", UpdateCarsRequest.class);

        Message<UpdateCarsRequest> message = MessageBuilder.withPayload(updateCarsRequest).build();

        when(carVectorSyncService.removeCar(anyString())).thenReturn(Mono.empty());
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofMinutes(0)));

        updateCarsMessageConsumer.updateCarsConsumer().apply(Flux.just(message))
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

}