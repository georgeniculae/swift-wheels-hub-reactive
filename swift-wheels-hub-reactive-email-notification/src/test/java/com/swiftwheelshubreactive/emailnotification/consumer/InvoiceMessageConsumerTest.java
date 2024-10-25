package com.swiftwheelshubreactive.emailnotification.consumer;

import com.sendgrid.Response;
import com.swiftwheelshubreactive.dto.EmailResponse;
import com.swiftwheelshubreactive.dto.InvoiceResponse;
import com.swiftwheelshubreactive.emailnotification.service.EmailProcessorService;
import com.swiftwheelshubreactive.emailnotification.util.TestUtil;
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
class InvoiceMessageConsumerTest {

    @InjectMocks
    private InvoiceMessageConsumer invoiceMessageConsumer;

    @Mock
    private EmailProcessorService emailProcessorService;

    @Mock
    private Acknowledgment acknowledgment;

    @Test
    void emailNotificationConsumerTest_success_acknowledgementTrue() {
        ReflectionTestUtils.setField(invoiceMessageConsumer, "isMessageAckEnabled", true);

        EmailResponse emailResponse =
                TestUtil.getResourceAsJson("/data/EmailResponse.json", EmailResponse.class);

        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        Response response = new Response();
        response.setStatusCode(200);
        response.setBody("body");

        MessageHeaders messageHeaders = new MessageHeaders(Map.of(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment));
        Message<InvoiceResponse> message = MessageBuilder.createMessage(invoiceResponse, messageHeaders);
        Flux<Message<InvoiceResponse>> messageFlux = Flux.just(message);

        when(emailProcessorService.sendEmail(any(InvoiceResponse.class))).thenReturn(Mono.just(emailResponse));

        StepVerifier.create(invoiceMessageConsumer.emailNotificationConsumer().apply(messageFlux))
                .expectComplete()
                .verify();
    }

    @Test
    void emailNotificationConsumerTest_acknowledgementTrue_noHeaders() {
        ReflectionTestUtils.setField(invoiceMessageConsumer, "isMessageAckEnabled", true);

        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        EmailResponse emailResponse =
                TestUtil.getResourceAsJson("/data/EmailResponse.json", EmailResponse.class);

        Flux<Message<InvoiceResponse>> messageFlux = Flux.just(new GenericMessage<>(invoiceResponse));

        when(emailProcessorService.sendEmail(any(InvoiceResponse.class))).thenReturn(Mono.just(emailResponse));

        StepVerifier.create(invoiceMessageConsumer.emailNotificationConsumer().apply(messageFlux))
                .expectComplete()
                .verify();
    }

}
