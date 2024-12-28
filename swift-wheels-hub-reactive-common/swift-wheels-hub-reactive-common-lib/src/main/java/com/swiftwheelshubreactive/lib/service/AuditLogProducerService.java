package com.swiftwheelshubreactive.lib.service;

import com.swiftwheelshubreactive.dto.AuditLogInfoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@ConditionalOnBean(name = "auditAspect")
public class AuditLogProducerService {

    private final StreamBridge streamBridge;

    @Value("${spring.cloud.stream.bindings.auditLogInfoProducer-out-0.destination}")
    private String binderName;

    @Value("${spring.cloud.stream.bindings.auditLogInfoProducer-out-0.contentType}")
    private String mimeType;

    public Mono<Void> sendAuditLog(AuditLogInfoRequest auditLogInfoRequest) {
        return Mono.fromRunnable(
                        () -> streamBridge.send(
                                binderName,
                                buildMessage(auditLogInfoRequest),
                                MimeType.valueOf(mimeType)
                        )
                )
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    private Message<AuditLogInfoRequest> buildMessage(AuditLogInfoRequest auditLogInfoRequest) {
        return MessageBuilder.withPayload(auditLogInfoRequest)
                .build();
    }

}
