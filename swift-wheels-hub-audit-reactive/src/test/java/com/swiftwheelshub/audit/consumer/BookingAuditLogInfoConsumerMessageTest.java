package com.swiftwheelshub.audit.consumer;

import com.swiftwheelshub.audit.service.AuditService;
import com.swiftwheelshub.audit.util.TestUtils;
import com.swiftwheelshub.dto.AuditLogInfoRequest;
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
class BookingAuditLogInfoConsumerMessageTest {

    @InjectMocks
    private BookingAuditLogInfoConsumerMessage bookingAuditLogInfoConsumerMessage;

    @Mock
    private AuditService auditService;

    @Mock
    private Acknowledgment acknowledgment;

    @Test
    void bookingAuditLogInfoConsumerTest_success_acknowledgementTrue() {
        ReflectionTestUtils.setField(bookingAuditLogInfoConsumerMessage, "isMessageAckEnabled", true);

        AuditLogInfoRequest auditLogInfoRequest =
                TestUtils.getResourceAsJson("/data/AuditLogInfoRequest.json", AuditLogInfoRequest.class);

        MessageHeaders messageHeaders = new MessageHeaders(Map.of(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment));
        Message<AuditLogInfoRequest> message = MessageBuilder.createMessage(auditLogInfoRequest, messageHeaders);
        Flux<Message<AuditLogInfoRequest>> messageFlux = Flux.just(message);

        when(auditService.saveAuditLogInfo(any(AuditLogInfoRequest.class))).thenReturn(Mono.just(auditLogInfoRequest));

        StepVerifier.create(bookingAuditLogInfoConsumerMessage.bookingAuditLogInfoConsumer().apply(messageFlux))
                .expectComplete()
                .verify();
    }

    @Test
    void bookingAuditLogInfoConsumerTest_acknowledgementTrue_noHeaders() {
        ReflectionTestUtils.setField(bookingAuditLogInfoConsumerMessage, "isMessageAckEnabled", true);

        AuditLogInfoRequest auditLogInfoDto =
                TestUtils.getResourceAsJson("/data/BookingAuditLogInfoDto.json", AuditLogInfoRequest.class);

        Message<AuditLogInfoRequest> message = new GenericMessage<>(auditLogInfoDto);
        Flux<Message<AuditLogInfoRequest>> messageFlux = Flux.just(message);

        when(auditService.saveAuditLogInfo(any(AuditLogInfoRequest.class))).thenReturn(Mono.just(auditLogInfoDto));

        StepVerifier.create(bookingAuditLogInfoConsumerMessage.bookingAuditLogInfoConsumer().apply(messageFlux))
                .expectComplete()
                .verify();
    }

}
