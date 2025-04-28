package com.autohubreactive.audit.consumer;

import com.autohubreactive.audit.service.AuditService;
import com.autohubreactive.dto.AuditLogInfoRequest;
import com.autohubreactive.lib.util.KafkaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class BookingAuditLogInfoConsumerMessage {

    private final AuditService auditService;

    @Bean
    public Function<Flux<Message<AuditLogInfoRequest>>, Mono<Void>> bookingAuditLogInfoConsumer() {
        return messageFlux -> messageFlux.concatMap(this::processMessage)
                .then();
    }

    private Mono<AuditLogInfoRequest> processMessage(Message<AuditLogInfoRequest> message) {
        return auditService.saveBookingAuditLogInfo(message.getPayload())
                .doOnNext(auditLogInfoRequest -> {
                    KafkaUtil.acknowledgeMessage(message.getHeaders());
                    log.info("Booking audit log saved: {}", auditLogInfoRequest);
                })
                .onErrorResume(e -> {
                    log.error("Exception during processing saved audit log message: {}", e.getMessage(), e);

                    return Mono.empty();
                });
    }

}
