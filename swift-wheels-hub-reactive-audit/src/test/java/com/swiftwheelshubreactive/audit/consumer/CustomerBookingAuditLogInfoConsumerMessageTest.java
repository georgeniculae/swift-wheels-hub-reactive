package com.swiftwheelshubreactive.audit.consumer;

import com.swiftwheelshubreactive.audit.service.AuditService;
import com.swiftwheelshubreactive.audit.util.TestUtil;
import com.swiftwheelshubreactive.dto.AuditLogInfoRequest;
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
class CustomerBookingAuditLogInfoConsumerMessageTest {

    @InjectMocks
    private CustomerAuditLogInfoConsumerMessage customerAuditLogInfoConsumerMessage;

    @Mock
    private AuditService auditService;

    @Mock
    private Acknowledgment acknowledgment;

    @Test
    void customerAuditLogInfoConsumerTest_success_acknowledgementTrue() {
        ReflectionTestUtils.setField(customerAuditLogInfoConsumerMessage, "isMessageAckEnabled", true);

        AuditLogInfoRequest auditLogInfoRequest =
                TestUtil.getResourceAsJson("/data/CustomerAuditLogInfoRequest.json", AuditLogInfoRequest.class);

        MessageHeaders messageHeaders = new MessageHeaders(Map.of(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment));
        Message<AuditLogInfoRequest> message = MessageBuilder.createMessage(auditLogInfoRequest, messageHeaders);
        Flux<Message<AuditLogInfoRequest>> messageFlux = Flux.just(message);

        when(auditService.saveCustomerAuditLogInfo(any(AuditLogInfoRequest.class))).thenReturn(Mono.just(auditLogInfoRequest));

        StepVerifier.create(customerAuditLogInfoConsumerMessage.customerAuditLogInfoConsumer().apply(messageFlux))
                .expectComplete()
                .verify();
    }

    @Test
    void customerAuditLogInfoConsumerTest_success_acknowledgementTrue_noHeaders() {
        ReflectionTestUtils.setField(customerAuditLogInfoConsumerMessage, "isMessageAckEnabled", true);

        AuditLogInfoRequest auditLogInfoRequest =
                TestUtil.getResourceAsJson("/data/CustomerAuditLogInfoRequest.json", AuditLogInfoRequest.class);

        Message<AuditLogInfoRequest> message = new GenericMessage<>(auditLogInfoRequest);
        Flux<Message<AuditLogInfoRequest>> messageFlux = Flux.just(message);

        when(auditService.saveCustomerAuditLogInfo(any(AuditLogInfoRequest.class))).thenReturn(Mono.just(auditLogInfoRequest));

        StepVerifier.create(customerAuditLogInfoConsumerMessage.customerAuditLogInfoConsumer().apply(messageFlux))
                .expectComplete()
                .verify();
    }

}
