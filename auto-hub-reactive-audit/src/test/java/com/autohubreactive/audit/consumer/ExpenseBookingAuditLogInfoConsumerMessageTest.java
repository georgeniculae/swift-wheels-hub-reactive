package com.autohubreactive.audit.consumer;

import com.autohubreactive.audit.service.AuditService;
import com.autohubreactive.audit.util.TestUtil;
import com.autohubreactive.dto.AuditLogInfoRequest;
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

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseBookingAuditLogInfoConsumerMessageTest {

    @InjectMocks
    private ExpenseAuditLogInfoConsumerMessage expenseAuditLogInfoConsumerMessage;

    @Mock
    private AuditService auditService;

    @Mock
    private Acknowledgment acknowledgment;

    @Test
    void expenseAuditLogInfoConsumerTest_success_acknowledgementTrue() {
        AuditLogInfoRequest auditLogInfoRequest =
                TestUtil.getResourceAsJson("/data/ExpenseAuditLogInfoRequest.json", AuditLogInfoRequest.class);

        MessageHeaders messageHeaders = new MessageHeaders(Map.of(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment));
        Message<AuditLogInfoRequest> message = MessageBuilder.createMessage(auditLogInfoRequest, messageHeaders);
        Flux<Message<AuditLogInfoRequest>> messageFlux = Flux.just(message);

        when(auditService.saveExpenseAuditLogInfo(any(AuditLogInfoRequest.class))).thenReturn(Mono.just(auditLogInfoRequest));

        StepVerifier.create(expenseAuditLogInfoConsumerMessage.expenseAuditLogInfoConsumer().apply(messageFlux))
                .expectComplete()
                .verify();
    }

    @Test
    void expenseAuditLogInfoConsumerTest_success_acknowledgementTrue_noHeaders() {
        AuditLogInfoRequest auditLogInfoDto =
                TestUtil.getResourceAsJson("/data/ExpenseAuditLogInfoRequest.json", AuditLogInfoRequest.class);

        Message<AuditLogInfoRequest> message = new GenericMessage<>(auditLogInfoDto);
        Flux<Message<AuditLogInfoRequest>> messageFlux = Flux.just(message);

        when(auditService.saveExpenseAuditLogInfo(any(AuditLogInfoRequest.class))).thenReturn(Mono.just(auditLogInfoDto));

        StepVerifier.create(expenseAuditLogInfoConsumerMessage.expenseAuditLogInfoConsumer().apply(messageFlux))
                .expectComplete()
                .verify();
    }

}
