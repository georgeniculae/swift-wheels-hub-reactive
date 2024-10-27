package com.swiftwheelshubreactive.emailnotification.consumer;

import com.swiftwheelshubreactive.dto.CustomerInfo;
import com.swiftwheelshubreactive.emailnotification.service.CustomerDetailsService;
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
class CustomerDetailsConsumerTest {

    @InjectMocks
    private CustomerDetailsConsumer customerDetailsConsumer;

    @Mock
    private CustomerDetailsService customerDetailsService;

    @Mock
    private Acknowledgment acknowledgment;

    @Test
    void emailNotificationConsumerTest_success_acknowledgementTrue() {
        ReflectionTestUtils.setField(customerDetailsConsumer, "isMessageAckEnabled", true);

        CustomerInfo customerInfo =
                TestUtil.getResourceAsJson("/data/CustomerInfo.json", CustomerInfo.class);

        MessageHeaders messageHeaders = new MessageHeaders(Map.of(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment));
        Message<CustomerInfo> message = MessageBuilder.createMessage(customerInfo, messageHeaders);
        Flux<Message<CustomerInfo>> messageFlux = Flux.just(message);

        when(customerDetailsService.saveCustomerDetails(any(CustomerInfo.class))).thenReturn(Mono.empty());

        StepVerifier.create(customerDetailsConsumer.customerInfoConsumer().apply(messageFlux))
                .expectComplete()
                .verify();
    }

    @Test
    void emailNotificationConsumerTest_acknowledgementTrue_noHeaders() {
        ReflectionTestUtils.setField(customerDetailsConsumer, "isMessageAckEnabled", true);

        CustomerInfo customerInfo =
                TestUtil.getResourceAsJson("/data/CustomerInfo.json", CustomerInfo.class);

        Flux<Message<CustomerInfo>> messageFlux = Flux.just(new GenericMessage<>(customerInfo));

        when(customerDetailsService.saveCustomerDetails(any(CustomerInfo.class))).thenReturn(Mono.empty());

        StepVerifier.create(customerDetailsConsumer.customerInfoConsumer().apply(messageFlux))
                .expectComplete()
                .verify();
    }

}
