package com.swiftwheelshubreactive.booking.consumer;

import com.swiftwheelshubreactive.booking.service.BookingService;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingRollbackMessageConsumerTest {

    @InjectMocks
    private BookingRollbackMessageConsumer bookingRollbackMessageConsumer;

    @Mock
    private Acknowledgment acknowledgment;

    @Mock
    private BookingService bookingService;

    @Mock
    private RetryHandler retryHandler;

    @Test
    void bookingRollbackConsumerTest_success() {
        Message<String> message = MessageBuilder.withPayload("id")
                .setHeader(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment)
                .build();

        Flux<Message<String>> messageFlux = Flux.just(message);

        when(bookingService.rollbackBooking(anyString())).thenReturn(Mono.empty());
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofMinutes(0)));

        bookingRollbackMessageConsumer.bookingRollbackConsumer().apply(messageFlux)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void bookingRollbackConsumerTest_noAcknowledgement() {
        Message<String> message = MessageBuilder.withPayload("id")
                .build();

        Flux<Message<String>> messageFlux = Flux.just(message);

        when(bookingService.rollbackBooking(anyString())).thenReturn(Mono.empty());
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofMinutes(0)));

        bookingRollbackMessageConsumer.bookingRollbackConsumer().apply(messageFlux)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

}