package com.swiftwheelshubreactive.expense.consumer;

import com.swiftwheelshubreactive.expense.service.InvoiceService;
import com.swiftwheelshubreactive.lib.retry.RetryHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.retry.RetrySpec;

import java.time.Duration;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeletedBookingMessageConsumerTest {

    @InjectMocks
    private DeletedBookingMessageConsumer deletedBookingMessageConsumer;

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private Acknowledgment acknowledgment;

    @Mock
    private RetryHandler retryHandler;

    @Test
    void deletedBookingConsumerTest_success_acknowledgedMessage() {
        MessageHeaders messageHeaders = new MessageHeaders(Map.of(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment));
        Message<String> message = MessageBuilder.createMessage("user", messageHeaders);

        when(invoiceService.deleteInvoiceByBookingId(anyString())).thenReturn(Mono.empty());
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofSeconds(0)));

        StepVerifier.create(deletedBookingMessageConsumer.deletedBookingConsumer().apply(Flux.just(message)))
                .expectComplete()
                .verify();
    }

    @Test
    void deletedBookingConsumerTest_error_notAcknowledgedMessage() {
        Message<String> message = MessageBuilder.withPayload("user").build();

        when(invoiceService.deleteInvoiceByBookingId(anyString())).thenReturn(Mono.error(new Throwable()));
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofSeconds(0)));

        StepVerifier.create(deletedBookingMessageConsumer.deletedBookingConsumer().apply(Flux.just(message)))
                .expectComplete()
                .verify();
    }

}
