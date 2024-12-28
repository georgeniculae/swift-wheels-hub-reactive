package com.swiftwheelshubreactive.agency.consumer;

import com.swiftwheelshubreactive.agency.service.CarService;
import com.swiftwheelshubreactive.agency.util.TestUtil;
import com.swiftwheelshubreactive.dto.CarStatusUpdate;
import com.swiftwheelshubreactive.lib.retry.RetryHandler;
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
class SaveBookingCarStatusUpdateMessageConsumerTest {

    @InjectMocks
    private SaveBookingCarStatusUpdateMessageConsumer saveBookingCarStatusUpdateMessageConsumer;

    @Mock
    private CarService carService;

    @Mock
    private Acknowledgment acknowledgment;

    @Mock
    private RetryHandler retryHandler;

    @Test
    void saveBookingCarUpdateConsumerTest_success() {
        CarStatusUpdate carStatusUpdate =
                TestUtil.getResourceAsJson("/data/CarStatusUpdate.json", CarStatusUpdate.class);

        Message<CarStatusUpdate> carStatusUpdateMessage = MessageBuilder.withPayload(carStatusUpdate)
                .setHeader(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment)
                .build();

        Flux<Message<CarStatusUpdate>> messageFlux = Flux.just(carStatusUpdateMessage);

        when(carService.updateCarStatus(any(CarStatusUpdate.class))).thenReturn(Mono.empty());
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofMinutes(0)));

        saveBookingCarStatusUpdateMessageConsumer.saveBookingCarUpdateConsumer().apply(messageFlux)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void saveBookingCarUpdateConsumerTest_noAcknowledgement() {
        CarStatusUpdate carStatusUpdate =
                TestUtil.getResourceAsJson("/data/CarStatusUpdate.json", CarStatusUpdate.class);

        Message<CarStatusUpdate> carStatusUpdateMessage = MessageBuilder.withPayload(carStatusUpdate)
                .build();

        Flux<Message<CarStatusUpdate>> messageFlux = Flux.just(carStatusUpdateMessage);

        when(carService.updateCarStatus(any(CarStatusUpdate.class))).thenReturn(Mono.empty());
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofMinutes(0)));

        saveBookingCarStatusUpdateMessageConsumer.saveBookingCarUpdateConsumer().apply(messageFlux)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

}
