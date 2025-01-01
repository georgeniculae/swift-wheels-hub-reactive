package com.swiftwheelshubreactive.agency.consumer;

import com.swiftwheelshubreactive.agency.service.CarService;
import com.swiftwheelshubreactive.agency.util.TestUtil;
import com.swiftwheelshubreactive.dto.UpdateCarsRequest;
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
class CarUpdateAfterBookingUpdateMessageConsumerTest {

    @InjectMocks
    private CarUpdateAfterBookingUpdateMessageConsumer carUpdateAfterBookingUpdateMessageConsumer;

    @Mock
    private CarService carService;

    @Mock
    private Acknowledgment acknowledgment;

    @Mock
    private RetryHandler retryHandler;

    @Test
    void carUpdateAfterInvoiceCloseConsumerTest_success() {
        UpdateCarsRequest updateCarsRequest =
                TestUtil.getResourceAsJson("/data/UpdateCarsRequest.json", UpdateCarsRequest.class);

        Message<UpdateCarsRequest> updateCarsRequestMessage = MessageBuilder.withPayload(updateCarsRequest)
                .setHeader(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment)
                .build();

        Flux<Message<UpdateCarsRequest>> messageFlux = Flux.just(updateCarsRequestMessage);

        when(carService.updateCarsStatus(any(UpdateCarsRequest.class))).thenReturn(Mono.empty());
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofMinutes(0)));

        carUpdateAfterBookingUpdateMessageConsumer.carUpdateAfterBookingUpdateConsumer().apply(messageFlux)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void carUpdateAfterInvoiceCloseConsumerTest_noAcknowledgement() {
        UpdateCarsRequest updateCarsRequest =
                TestUtil.getResourceAsJson("/data/UpdateCarsRequest.json", UpdateCarsRequest.class);

        Message<UpdateCarsRequest> updateCarsRequestMessage = MessageBuilder.withPayload(updateCarsRequest)
                .build();

        Flux<Message<UpdateCarsRequest>> messageFlux = Flux.just(updateCarsRequestMessage);

        when(carService.updateCarsStatus(any(UpdateCarsRequest.class))).thenReturn(Mono.empty());
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofMinutes(0)));

        carUpdateAfterBookingUpdateMessageConsumer.carUpdateAfterBookingUpdateConsumer().apply(messageFlux)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

}
