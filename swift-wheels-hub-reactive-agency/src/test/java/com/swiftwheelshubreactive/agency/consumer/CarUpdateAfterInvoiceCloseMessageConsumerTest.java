package com.swiftwheelshubreactive.agency.consumer;

import com.swiftwheelshubreactive.agency.service.CarService;
import com.swiftwheelshubreactive.agency.util.TestUtil;
import com.swiftwheelshubreactive.dto.CarUpdateDetails;
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
class CarUpdateAfterInvoiceCloseMessageConsumerTest {

    @InjectMocks
    private CarUpdateAfterInvoiceCloseMessageConsumer carUpdateAfterInvoiceCloseMessageConsumer;

    @Mock
    private CarService carService;

    @Mock
    private Acknowledgment acknowledgment;

    @Mock
    private RetryHandler retryHandler;

    @Test
    void invoiceCarUpdateConsumerTest_success() {
        CarUpdateDetails carUpdateDetails =
                TestUtil.getResourceAsJson("/data/CarUpdateDetails.json", CarUpdateDetails.class);

        Message<CarUpdateDetails> carUpdateDetailsMessage = MessageBuilder.withPayload(carUpdateDetails)
                .setHeader(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment)
                .build();

        Flux<Message<CarUpdateDetails>> messageFlux = Flux.just(carUpdateDetailsMessage);

        when(carService.updateCarWhenBookingIsClosed(any(CarUpdateDetails.class))).thenReturn(Mono.empty());
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofMinutes(0)));

        carUpdateAfterInvoiceCloseMessageConsumer.carUpdateAfterInvoiceCloseConsumer().apply(messageFlux)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void invoiceCarUpdateConsumerTest_noAcknowledgement() {
        CarUpdateDetails carUpdateDetails =
                TestUtil.getResourceAsJson("/data/CarUpdateDetails.json", CarUpdateDetails.class);

        Message<CarUpdateDetails> carUpdateDetailsMessage = MessageBuilder.withPayload(carUpdateDetails)
                .build();

        Flux<Message<CarUpdateDetails>> messageFlux = Flux.just(carUpdateDetailsMessage);

        when(carService.updateCarWhenBookingIsClosed(any(CarUpdateDetails.class))).thenReturn(Mono.empty());
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofMinutes(0)));

        carUpdateAfterInvoiceCloseMessageConsumer.carUpdateAfterInvoiceCloseConsumer().apply(messageFlux)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

}
