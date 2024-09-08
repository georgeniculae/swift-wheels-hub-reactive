package com.swiftwheelshubreactive.audit.consumer;

import com.swiftwheelshubreactive.audit.service.AuditService;
import com.swiftwheelshubreactive.dto.AuditLogInfoRequest;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubResponseStatusException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class BookingAuditLogInfoConsumerMessage {

    private final AuditService auditService;

    @Value("${auditConsumer.isMessageAckEnabled:false}")
    private boolean isMessageAckEnabled;

    @Bean
    public Function<Flux<Message<AuditLogInfoRequest>>, Mono<Void>> bookingAuditLogInfoConsumer() {
        return messageFlux -> messageFlux.concatMap(this::processMessage)
                .then();
    }

    private Mono<AuditLogInfoRequest> processMessage(Message<AuditLogInfoRequest> message) {
        return auditService.saveBookingAuditLogInfo(message.getPayload())
                .doOnNext(auditLogInfoRequest -> {
                    log.info("Booking audit log saved: {}", auditLogInfoRequest);

                    if (isMessageAckEnabled) {
                        sendMessageAcknowledgement(message.getHeaders());
                    }
                })
                .onErrorResume(e -> {
                    log.error("Exception during processing saved audit log message: {}", e.getMessage(), e);

                    return Mono.empty();
                });
    }

    private void sendMessageAcknowledgement(MessageHeaders messageHeaders) {
        Optional.ofNullable(messageHeaders.get(KafkaHeaders.ACKNOWLEDGMENT, Acknowledgment.class))
                .orElseThrow(
                        () -> new SwiftWheelsHubResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "There is no Kafka acknowledgement in message headers"
                        )
                )
                .acknowledge();
    }

}
