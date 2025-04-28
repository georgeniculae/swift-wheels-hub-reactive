package com.autohubreactive.expense.consumer;

import com.autohubreactive.dto.BookingResponse;
import com.autohubreactive.dto.InvoiceResponse;
import com.autohubreactive.expense.service.InvoiceService;
import com.autohubreactive.expense.util.TestUtil;
import com.autohubreactive.lib.retry.RetryHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.retry.RetrySpec;

import java.time.Duration;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreatedBookingMessageConsumerTest {

    @InjectMocks
    private CreatedBookingMessageConsumer createdBookingMessageConsumer;

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private Acknowledgment acknowledgment;

    @Mock
    private RetryHandler retryHandler;

    @Test
    void savedBookingConsumerTest_success_acknowledgedMessage() {
        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        MessageHeaders messageHeaders = new MessageHeaders(Map.of(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment));
        Message<BookingResponse> message = MessageBuilder.createMessage(bookingResponse, messageHeaders);

        when(invoiceService.saveInvoice(any(BookingResponse.class))).thenReturn(Mono.just(invoiceResponse));
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofSeconds(0)));

        StepVerifier.create(createdBookingMessageConsumer.savedBookingConsumer().apply(Flux.just(message)))
                .expectComplete()
                .verify();
    }

    @Test
    void savedBookingConsumerTest_success_notAcknowledgedMessage() {
        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        Message<BookingResponse> message = new GenericMessage<>(bookingResponse);

        when(invoiceService.saveInvoice(any(BookingResponse.class))).thenReturn(Mono.just(invoiceResponse));
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofSeconds(0)));

        StepVerifier.create(createdBookingMessageConsumer.savedBookingConsumer().apply(Flux.just(message)))
                .expectComplete()
                .verify();
    }

    @Test
    void savedBookingConsumerTest_success_emptyHeaders() {
        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        Message<BookingResponse> message = new GenericMessage<>(bookingResponse);

        when(invoiceService.saveInvoice(any(BookingResponse.class))).thenReturn(Mono.just(invoiceResponse));
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofSeconds(0)));

        StepVerifier.create(createdBookingMessageConsumer.savedBookingConsumer().apply(Flux.just(message)))
                .expectComplete()
                .verify();
    }

    @Test
    void savedBookingConsumerTest_errorSavingInvoice() {
        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        MessageHeaders messageHeaders = new MessageHeaders(Map.of(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment));
        Message<BookingResponse> message = MessageBuilder.createMessage(bookingResponse, messageHeaders);

        when(invoiceService.saveInvoice(any(BookingResponse.class))).thenReturn(Mono.error(new Throwable()));
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofSeconds(0)));

        StepVerifier.create(createdBookingMessageConsumer.savedBookingConsumer().apply(Flux.just(message)))
                .expectComplete()
                .verify();
    }

}
