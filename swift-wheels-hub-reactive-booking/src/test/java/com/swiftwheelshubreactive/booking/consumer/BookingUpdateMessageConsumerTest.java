package com.swiftwheelshubreactive.booking.consumer;

import com.swiftwheelshubreactive.booking.service.BookingService;
import com.swiftwheelshubreactive.booking.util.TestUtil;
import com.swiftwheelshubreactive.dto.BookingClosingDetails;
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
class BookingUpdateMessageConsumerTest {

    @InjectMocks
    private BookingUpdateMessageConsumer bookingUpdateMessageConsumer;

    @Mock
    private Acknowledgment acknowledgment;

    @Mock
    private BookingService bookingService;

    @Mock
    private RetryHandler retryHandler;

    @Test
    void bookingUpdateConsumerTest_success() {
        BookingClosingDetails bookingClosingDetails =
                TestUtil.getResourceAsJson("/data/BookingClosingDetails.json", BookingClosingDetails.class);

        Message<BookingClosingDetails> message = MessageBuilder.withPayload(bookingClosingDetails)
                .setHeader(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment)
                .build();

        Flux<Message<BookingClosingDetails>> messageFlux = Flux.just(message);

        when(bookingService.closeBooking(any(BookingClosingDetails.class))).thenReturn(Mono.empty());
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofMinutes(0)));

        bookingUpdateMessageConsumer.bookingUpdateConsumer().apply(messageFlux)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void bookingUpdateConsumerTest_noAcknowledgement() {
        BookingClosingDetails bookingClosingDetails =
                TestUtil.getResourceAsJson("/data/BookingClosingDetails.json", BookingClosingDetails.class);

        Message<BookingClosingDetails> message = MessageBuilder.withPayload(bookingClosingDetails)
                .build();

        Flux<Message<BookingClosingDetails>> messageFlux = Flux.just(message);

        when(bookingService.closeBooking(any(BookingClosingDetails.class))).thenReturn(Mono.empty());
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofMinutes(0)));

        bookingUpdateMessageConsumer.bookingUpdateConsumer().apply(messageFlux)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

}
