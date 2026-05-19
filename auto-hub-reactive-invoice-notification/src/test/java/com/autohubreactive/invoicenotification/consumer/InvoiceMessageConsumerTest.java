package com.autohubreactive.invoicenotification.consumer;

import com.autohubreactive.dto.common.InvoiceResponse;
import com.autohubreactive.invoicenotification.service.EmailProcessorService;
import com.autohubreactive.invoicenotification.util.TestUtil;
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
class InvoiceMessageConsumerTest {

    @InjectMocks
    private InvoiceMessageConsumer invoiceMessageConsumer;

    @Mock
    private EmailProcessorService emailProcessorService;

    @Mock
    private Acknowledgment acknowledgment;

    @Mock
    private RetryHandler retryHandler;

    @Test
    void invoiceNotificationConsumerTest_success_acknowledgementTrue() {
        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        MessageHeaders messageHeaders = new MessageHeaders(Map.of(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment));
        Message<InvoiceResponse> message = MessageBuilder.createMessage(invoiceResponse, messageHeaders);
        Flux<Message<InvoiceResponse>> messageFlux = Flux.just(message);

        when(emailProcessorService.sendEmail(any(InvoiceResponse.class))).thenReturn(Mono.empty());
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofSeconds(0)));

        invoiceMessageConsumer.invoiceNotificationConsumer().apply(messageFlux)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void invoiceNotificationConsumerTest_noKafkaHeaders() {
        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        Flux<Message<InvoiceResponse>> messageFlux = Flux.just(new GenericMessage<>(invoiceResponse));

        when(emailProcessorService.sendEmail(any(InvoiceResponse.class))).thenReturn(Mono.empty());
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofSeconds(0)));

        invoiceMessageConsumer.invoiceNotificationConsumer().apply(messageFlux)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

}
