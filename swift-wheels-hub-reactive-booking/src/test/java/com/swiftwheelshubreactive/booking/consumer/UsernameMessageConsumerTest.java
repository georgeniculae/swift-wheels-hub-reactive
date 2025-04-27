package com.swiftwheelshubreactive.booking.consumer;

import com.swiftwheelshubreactive.booking.service.bookingprocessing.BookingService;
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
class UsernameMessageConsumerTest {

    @InjectMocks
    private UsernameMessageConsumer usernameMessageConsumer;

    @Mock
    private Acknowledgment acknowledgment;

    @Mock
    private BookingService bookingService;

    @Mock
    private RetryHandler retryHandler;

    @Test
    void bookingUpdateConsumerTest_success() {
        Message<String> message = MessageBuilder.withPayload("username")
                .setHeader(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment)
                .build();

        Flux<Message<String>> messageFlux = Flux.just(message);

        when(bookingService.deleteBookingByCustomerUsername(anyString())).thenReturn(Mono.empty());
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofMinutes(0)));

        usernameMessageConsumer.usernameConsumer().apply(messageFlux)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void bookingUpdateConsumerTest_noAcknowledgement() {
        Message<String> message = MessageBuilder.withPayload("username")
                .build();

        Flux<Message<String>> messageFlux = Flux.just(message);

        when(bookingService.deleteBookingByCustomerUsername(anyString())).thenReturn(Mono.empty());
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofMinutes(0)));

        usernameMessageConsumer.usernameConsumer().apply(messageFlux)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

}
