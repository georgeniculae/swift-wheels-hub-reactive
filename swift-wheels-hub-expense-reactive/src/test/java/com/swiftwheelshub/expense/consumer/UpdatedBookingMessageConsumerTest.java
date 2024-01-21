package com.swiftwheelshub.expense.consumer;

import com.swiftwheelshub.dto.BookingDto;
import com.swiftwheelshub.dto.InvoiceDto;
import com.swiftwheelshub.expense.service.InvoiceService;
import com.swiftwheelshub.expense.util.TestUtils;
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

        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);
        InvoiceDto invoiceDto = TestUtils.getResourceAsJson("/data/InvoiceDto.json", InvoiceDto.class);

        MessageHeaders messageHeaders = new MessageHeaders(Map.of(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment));
        Message<BookingDto> message = MessageBuilder.createMessage(bookingDto, messageHeaders);

        when(invoiceService.updateInvoiceAfterBookingUpdate(any(BookingDto.class))).thenReturn(Mono.just(invoiceDto));

        StepVerifier.create(updatedBookingMessageConsumer.updatedBookingConsumer().apply(Flux.just(message)))
                .expectComplete()
                .verify();
    }

    @Test
    void savedBookingConsumerTest_success_notAcknowledgedMessage() {
        ReflectionTestUtils.setField(updatedBookingMessageConsumer, "isMessageAckEnabled", false);

        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);
        InvoiceDto invoiceDto = TestUtils.getResourceAsJson("/data/InvoiceDto.json", InvoiceDto.class);

        Message<BookingDto> message = new GenericMessage<>(bookingDto);

        when(invoiceService.updateInvoiceAfterBookingUpdate(any(BookingDto.class))).thenReturn(Mono.just(invoiceDto));

        StepVerifier.create(updatedBookingMessageConsumer.updatedBookingConsumer().apply(Flux.just(message)))
                .expectComplete()
                .verify();
    }

    @Test
    void savedBookingConsumerTest_success_emptyHeaders() {
        ReflectionTestUtils.setField(updatedBookingMessageConsumer, "isMessageAckEnabled", true);

        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);
        InvoiceDto invoiceDto = TestUtils.getResourceAsJson("/data/InvoiceDto.json", InvoiceDto.class);

        Message<BookingDto> message = new GenericMessage<>(bookingDto);

        when(invoiceService.updateInvoiceAfterBookingUpdate(any(BookingDto.class))).thenReturn(Mono.just(invoiceDto));

        StepVerifier.create(updatedBookingMessageConsumer.updatedBookingConsumer().apply(Flux.just(message)))
                .expectComplete()
                .verify();
    }

    @Test
    void savedBookingConsumerTest_errorSavingInvoice() {
        ReflectionTestUtils.setField(updatedBookingMessageConsumer, "isMessageAckEnabled", true);

        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        MessageHeaders messageHeaders = new MessageHeaders(Map.of(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment));
        Message<BookingDto> message = MessageBuilder.createMessage(bookingDto, messageHeaders);

        when(invoiceService.updateInvoiceAfterBookingUpdate(any(BookingDto.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(updatedBookingMessageConsumer.updatedBookingConsumer().apply(Flux.just(message)))
                .expectComplete()
                .verify();
    }

}
