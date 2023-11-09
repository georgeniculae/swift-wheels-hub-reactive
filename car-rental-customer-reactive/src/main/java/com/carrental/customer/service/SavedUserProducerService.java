package com.carrental.customer.service;

import com.carrental.dto.UserDto;
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
public class SavedUserProducerService {

    @Value("${spring.cloud.stream.bindings.savedUserProducer-out-0.destination}")
    private String savedUserBinderName;

    @Value("${spring.cloud.stream.bindings.savedUserProducer-out-0.contentType}")
    private String savedUserMimeType;

    private final StreamBridge streamBridge;

    public Mono<Boolean> sendSavedUser(UserDto userDto) {
        return Mono.fromCallable(() -> streamBridge.send(savedUserBinderName, buildMessage(userDto), MimeType.valueOf(savedUserMimeType)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Message<UserDto> buildMessage(UserDto userDto) {
        return MessageBuilder.withPayload(userDto)
                .build();
    }

}
