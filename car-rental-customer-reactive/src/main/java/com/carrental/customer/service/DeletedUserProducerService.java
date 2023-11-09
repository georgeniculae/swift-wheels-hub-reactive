package com.carrental.customer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeletedUserProducerService {

    @Value("${spring.cloud.stream.bindings.deletedUserProducer-out-0.destination}")
    private String deletedUserBinderName;

    @Value("${spring.cloud.stream.bindings.deletedUserProducer-out-0.contentType}")
    private String deletedUserMimeType;

    private final StreamBridge streamBridge;

    public Mono<Boolean> sendDeletedUser(String username) {
        return Mono.fromCallable(() -> streamBridge.send(deletedUserBinderName, buildMessage(username), MimeType.valueOf(deletedUserMimeType)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Message<String> buildMessage(String username) {
        return MessageBuilder.withPayload(username)
                .build();
    }

}
