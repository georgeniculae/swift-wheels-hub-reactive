package com.autohub.ai.consumer;

import com.autohubreactive.ai.consumer.CarStatusUpdateMessageConsumer;
import com.autohubreactive.ai.service.CarVectorSyncService;
import com.autohubreactive.dto.common.CarState;
import com.autohubreactive.dto.common.CarStatusUpdate;
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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarStatusUpdateMessageConsumerTest {

    @InjectMocks
    private CarStatusUpdateMessageConsumer carStatusUpdateMessageConsumer;

    @Mock
    private CarVectorSyncService carVectorSyncService;

    @Mock
    private Acknowledgment acknowledgment;

    @Mock
    private RetryHandler retryHandler;

    @Test
    void carStatusUpdateConsumerTest_notAvailable_success() {
        CarStatusUpdate carStatusUpdate = CarStatusUpdate.builder()
                .carId("64f361caf291ae086e179547")
                .carState(CarState.NOT_AVAILABLE)
                .build();

        Message<CarStatusUpdate> message = MessageBuilder.withPayload(carStatusUpdate)
                .setHeader(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment)
                .build();

        when(carVectorSyncService.removeCar(anyString())).thenReturn(Mono.empty());
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofMinutes(0)));

        carStatusUpdateMessageConsumer.carStatusUpdateConsumer().apply(Flux.just(message))
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void carStatusUpdateConsumerTest_notAvailable_noAcknowledgement() {
        CarStatusUpdate carStatusUpdate = CarStatusUpdate.builder()
                .carId("64f361caf291ae086e179547")
                .carState(CarState.NOT_AVAILABLE)
                .build();

        Message<CarStatusUpdate> message = MessageBuilder.withPayload(carStatusUpdate).build();

        when(carVectorSyncService.removeCar(anyString())).thenReturn(Mono.empty());
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofMinutes(0)));

        carStatusUpdateMessageConsumer.carStatusUpdateConsumer().apply(Flux.just(message))
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void carStatusUpdateConsumerTest_available_skipsVectorStore() {
        CarStatusUpdate carStatusUpdate = CarStatusUpdate.builder()
                .carId("64f361caf291ae086e179547")
                .carState(CarState.AVAILABLE)
                .build();

        Message<CarStatusUpdate> message = MessageBuilder.withPayload(carStatusUpdate)
                .setHeader(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment)
                .build();

        carStatusUpdateMessageConsumer.carStatusUpdateConsumer().apply(Flux.just(message))
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        verifyNoInteractions(carVectorSyncService);
    }

}