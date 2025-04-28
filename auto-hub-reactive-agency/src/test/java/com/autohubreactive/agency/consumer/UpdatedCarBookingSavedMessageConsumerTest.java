package com.autohubreactive.agency.consumer;

import com.autohubreactive.agency.service.CarService;
import com.autohubreactive.agency.util.TestUtil;
import com.autohubreactive.dto.CarStatusUpdate;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdatedCarBookingSavedMessageConsumerTest {

    @InjectMocks
    private UpdatedCarBookingSavedMessageConsumer updatedCarBookingSavedMessageConsumer;

    @Mock
    private CarService carService;

    @Mock
    private Acknowledgment acknowledgment;

    @Mock
    private RetryHandler retryHandler;

    @Test
    void carUpdateAfterBookingSaveConsumerTest_success() {
        CarStatusUpdate carStatusUpdate =
                TestUtil.getResourceAsJson("/data/CarStatusUpdate.json", CarStatusUpdate.class);

        Message<CarStatusUpdate> carStatusUpdateMessage = MessageBuilder.withPayload(carStatusUpdate)
                .setHeader(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment)
                .build();

        Flux<Message<CarStatusUpdate>> messageFlux = Flux.just(carStatusUpdateMessage);

        when(carService.updateCarStatus(any(CarStatusUpdate.class))).thenReturn(Mono.empty());
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofMinutes(0)));

        updatedCarBookingSavedMessageConsumer.updatedCarBookingSavedConsumer().apply(messageFlux)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void carUpdateAfterBookingSaveConsumerTest_noAcknowledgement() {
        CarStatusUpdate carStatusUpdate =
                TestUtil.getResourceAsJson("/data/CarStatusUpdate.json", CarStatusUpdate.class);

        Message<CarStatusUpdate> carStatusUpdateMessage = MessageBuilder.withPayload(carStatusUpdate)
                .build();

        Flux<Message<CarStatusUpdate>> messageFlux = Flux.just(carStatusUpdateMessage);

        when(carService.updateCarStatus(any(CarStatusUpdate.class))).thenReturn(Mono.empty());
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofMinutes(0)));

        updatedCarBookingSavedMessageConsumer.updatedCarBookingSavedConsumer().apply(messageFlux)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

}
