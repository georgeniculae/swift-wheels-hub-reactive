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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingBookingAuditLogInfoConsumerMessageTest {

    @InjectMocks
    private BookingAuditLogInfoConsumerMessage bookingAuditLogInfoConsumerMessage;

    @Mock
    private AuditService auditService;

    @Mock
    private Acknowledgment acknowledgment;

    @Test
    void bookingAuditLogInfoConsumerTest_success_acknowledgementTrue() {
        AuditLogInfoRequest auditLogInfoRequest =
                TestUtil.getResourceAsJson("/data/BookingAuditLogInfoRequest.json", AuditLogInfoRequest.class);

        MessageHeaders messageHeaders = new MessageHeaders(Map.of(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment));
        Message<AuditLogInfoRequest> message = MessageBuilder.createMessage(auditLogInfoRequest, messageHeaders);
        Flux<Message<AuditLogInfoRequest>> messageFlux = Flux.just(message);

        when(auditService.saveBookingAuditLogInfo(any(AuditLogInfoRequest.class))).thenReturn(Mono.just(auditLogInfoRequest));

        StepVerifier.create(bookingAuditLogInfoConsumerMessage.bookingAuditLogInfoConsumer().apply(messageFlux))
                .expectComplete()
                .verify();
    }

    @Test
    void bookingAuditLogInfoConsumerTest_acknowledgementTrue_noHeaders() {
        AuditLogInfoRequest auditLogInfoDto =
                TestUtil.getResourceAsJson("/data/BookingAuditLogInfoRequest.json", AuditLogInfoRequest.class);

        Message<AuditLogInfoRequest> message = new GenericMessage<>(auditLogInfoDto);
        Flux<Message<AuditLogInfoRequest>> messageFlux = Flux.just(message);

        when(auditService.saveBookingAuditLogInfo(any(AuditLogInfoRequest.class))).thenReturn(Mono.just(auditLogInfoDto));

        StepVerifier.create(bookingAuditLogInfoConsumerMessage.bookingAuditLogInfoConsumer().apply(messageFlux))
                .expectComplete()
                .verify();
    }

}
