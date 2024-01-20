package com.carrental.expense.consumer;

import com.carrental.expense.service.InvoiceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeletedBookingMessageConsumerTest {

    @InjectMocks
    private DeletedBookingMessageConsumer deletedBookingMessageConsumer;

    @Mock
    private InvoiceService invoiceService;

    @Test
    void deletedBookingConsumerTest_success_acknowledgedMessage() {
        Message<String> message = new GenericMessage<>("user");

        when(invoiceService.deleteInvoiceByBookingId(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(deletedBookingMessageConsumer.deletedBookingConsumer().apply(Flux.just(message)))
                .expectComplete()
                .verify();
    }

    @Test
    void savedBookingConsumerTest_success_notAcknowledgedMessage() {
        Message<String> message = new GenericMessage<>("user");

        when(invoiceService.deleteInvoiceByBookingId(anyString())).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(deletedBookingMessageConsumer.deletedBookingConsumer().apply(Flux.just(message)))
                .expectComplete()
                .verify();
    }

}
