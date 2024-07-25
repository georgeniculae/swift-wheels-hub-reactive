package com.swiftwheelshubreactive.expense.consumer;

import com.swiftwheelshubreactive.dto.BookingResponse;
import com.swiftwheelshubreactive.dto.InvoiceResponse;
import com.swiftwheelshubreactive.expense.service.InvoiceService;
import com.swiftwheelshubreactive.expense.util.TestUtil;
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
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdatedBookingMessageConsumerTest {

    @InjectMocks
    private UpdatedBookingMessageConsumer updatedBookingMessageConsumer;

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private Acknowledgment acknowledgment;

    @Test
    void savedBookingConsumerTest_success_acknowledgedMessage() {
        ReflectionTestUtils.setField(updatedBookingMessageConsumer, "isMessageAckEnabled", true);

        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        MessageHeaders messageHeaders = new MessageHeaders(Map.of(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment));
        Message<BookingResponse> message = MessageBuilder.createMessage(bookingResponse, messageHeaders);

        when(invoiceService.updateInvoiceAfterBookingUpdate(any(BookingResponse.class))).thenReturn(Mono.just(invoiceResponse));

        StepVerifier.create(updatedBookingMessageConsumer.updatedBookingConsumer().apply(Flux.just(message)))
                .expectComplete()
                .verify();
    }

    @Test
    void savedBookingConsumerTest_success_notAcknowledgedMessage() {
        ReflectionTestUtils.setField(updatedBookingMessageConsumer, "isMessageAckEnabled", false);

        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        Message<BookingResponse> message = new GenericMessage<>(bookingResponse);

        when(invoiceService.updateInvoiceAfterBookingUpdate(any(BookingResponse.class))).thenReturn(Mono.just(invoiceResponse));

        StepVerifier.create(updatedBookingMessageConsumer.updatedBookingConsumer().apply(Flux.just(message)))
                .expectComplete()
                .verify();
    }

    @Test
    void savedBookingConsumerTest_success_emptyHeaders() {
        ReflectionTestUtils.setField(updatedBookingMessageConsumer, "isMessageAckEnabled", true);

        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        Message<BookingResponse> message = new GenericMessage<>(bookingResponse);

        when(invoiceService.updateInvoiceAfterBookingUpdate(any(BookingResponse.class))).thenReturn(Mono.just(invoiceResponse));

        StepVerifier.create(updatedBookingMessageConsumer.updatedBookingConsumer().apply(Flux.just(message)))
                .expectComplete()
                .verify();
    }

    @Test
    void savedBookingConsumerTest_errorSavingInvoice() {
        ReflectionTestUtils.setField(updatedBookingMessageConsumer, "isMessageAckEnabled", true);

        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        MessageHeaders messageHeaders = new MessageHeaders(Map.of(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment));
        Message<BookingResponse> message = MessageBuilder.createMessage(bookingResponse, messageHeaders);

        when(invoiceService.updateInvoiceAfterBookingUpdate(any(BookingResponse.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(updatedBookingMessageConsumer.updatedBookingConsumer().apply(Flux.just(message)))
                .expectComplete()
                .verify();
    }

}
