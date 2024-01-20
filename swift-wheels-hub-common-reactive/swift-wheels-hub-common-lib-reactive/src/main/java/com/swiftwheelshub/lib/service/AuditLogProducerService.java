package com.swiftwheelshub.lib.service;

import com.swiftwheelshub.dto.AuditLogInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AuditLogProducerService {

    @Value("${spring.cloud.stream.bindings.auditLogInfoProducer-out-0.destination}")
    private String auditLogBinderName;

    @Value("${spring.cloud.stream.bindings.auditLogInfoProducer-out-0.contentType}")
    private String auditLogMimeType;

    private final StreamBridge streamBridge;

    public Mono<Void> sendAuditLog(AuditLogInfoDto auditLogInfoDto) {
        return Mono.fromRunnable(() -> streamBridge.send(auditLogBinderName, buildMessage(auditLogInfoDto), MimeType.valueOf(auditLogMimeType)))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    private Message<AuditLogInfoDto> buildMessage(AuditLogInfoDto auditLogInfoDto) {
        return MessageBuilder.withPayload(auditLogInfoDto)
                .build();
    }

}
