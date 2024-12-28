package com.swiftwheelshubreactive.expense.consumer;

import com.swiftwheelshubreactive.dto.InvoiceReprocessRequest;
import com.swiftwheelshubreactive.expense.service.InvoiceReprocessingService;
import com.swiftwheelshubreactive.expense.util.TestUtil;
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
class FailedInvoiceDlqMessageConsumerTest {

    @InjectMocks
    private FailedInvoiceDlqMessageConsumer failedInvoiceDlqMessageConsumer;

    @Mock
    private InvoiceReprocessingService invoiceReprocessingService;

    @Mock
    private Acknowledgment acknowledgment;

    @Mock
    private RetryHandler retryHandler;

    @Test
    void failedInvoiceDlqConsumerTest_success_withAcknowledgement() {
        InvoiceReprocessRequest invoiceReprocessRequest =
                TestUtil.getResourceAsJson("/data/InvoiceReprocessRequest.json", InvoiceReprocessRequest.class);

        Message<InvoiceReprocessRequest> message = MessageBuilder.withPayload(invoiceReprocessRequest)
                .setHeader(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment)
                .build();

        when(invoiceReprocessingService.reprocessInvoice(any(InvoiceReprocessRequest.class))).thenReturn(Mono.empty());
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofMinutes(0)));

        StepVerifier.create(failedInvoiceDlqMessageConsumer.failedInvoiceDlqConsumer().apply(Flux.just(message)))
                .expectComplete()
                .verify();
    }

    @Test
    void failedInvoiceDlqConsumerTest_success_noAcknowledgement() {
        InvoiceReprocessRequest invoiceReprocessRequest =
                TestUtil.getResourceAsJson("/data/InvoiceReprocessRequest.json", InvoiceReprocessRequest.class);

        Message<InvoiceReprocessRequest> message = MessageBuilder.withPayload(invoiceReprocessRequest)
                .build();

        when(invoiceReprocessingService.reprocessInvoice(any(InvoiceReprocessRequest.class))).thenReturn(Mono.empty());
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofMinutes(0)));

        StepVerifier.create(failedInvoiceDlqMessageConsumer.failedInvoiceDlqConsumer().apply(Flux.just(message)))
                .expectComplete()
                .verify();
    }

}
