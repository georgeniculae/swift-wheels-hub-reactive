package com.swiftwheelshub.audit.consumer;

import com.swiftwheelshub.audit.service.AuditService;
import com.swiftwheelshub.audit.util.TestUtils;
import com.swiftwheelshub.dto.AuditLogInfoDto;
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
class ExpenseAuditLogInfoConsumerMessageTest {

    @InjectMocks
    private ExpenseAuditLogInfoConsumerMessage expenseAuditLogInfoConsumerMessage;

    @Mock
    private AuditService auditService;

    @Mock
    private Acknowledgment acknowledgment;

    @Test
    void expenseAuditLogInfoConsumerTest_success_acknowledgementTrue() {
        ReflectionTestUtils.setField(expenseAuditLogInfoConsumerMessage, "isMessageAckEnabled", true);

        AuditLogInfoDto auditLogInfoDto =
                TestUtils.getResourceAsJson("/data/ExpenseAuditLogInfoDto.json", AuditLogInfoDto.class);

        MessageHeaders messageHeaders = new MessageHeaders(Map.of(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment));
        Message<AuditLogInfoDto> message = MessageBuilder.createMessage(auditLogInfoDto, messageHeaders);
        Flux<Message<AuditLogInfoDto>> messageFlux = Flux.just(message);

        when(auditService.saveAuditLogInfo(any(AuditLogInfoDto.class))).thenReturn(Mono.just(auditLogInfoDto));

        StepVerifier.create(expenseAuditLogInfoConsumerMessage.expenseAuditLogInfoConsumer().apply(messageFlux))
                .expectComplete()
                .verify();
    }

    @Test
    void expenseAuditLogInfoConsumerTest_success_acknowledgementTrue_noHeaders() {
        ReflectionTestUtils.setField(expenseAuditLogInfoConsumerMessage, "isMessageAckEnabled", true);

        AuditLogInfoDto auditLogInfoDto =
                TestUtils.getResourceAsJson("/data/ExpenseAuditLogInfoDto.json", AuditLogInfoDto.class);

        Message<AuditLogInfoDto> message = new GenericMessage<>(auditLogInfoDto);
        Flux<Message<AuditLogInfoDto>> messageFlux = Flux.just(message);

        when(auditService.saveAuditLogInfo(any(AuditLogInfoDto.class))).thenReturn(Mono.just(auditLogInfoDto));

        StepVerifier.create(expenseAuditLogInfoConsumerMessage.expenseAuditLogInfoConsumer().apply(messageFlux))
                .expectComplete()
                .verify();
    }

}
