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
public class UpdatedUserProducerService {

    @Value("${spring.cloud.stream.bindings.updatedUserProducer-out-0.destination}")
    private String updatedUserBinderName;

    @Value("${spring.cloud.stream.bindings.updatedUserProducer-out-0.contentType}")
    private String updatedUserMimeType;

    private final StreamBridge streamBridge;

    public Mono<Boolean> sendUpdatedUser(UserDto userDto) {
        return Mono.fromCallable(() -> streamBridge.send(updatedUserBinderName, buildMessage(userDto), MimeType.valueOf(updatedUserMimeType)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Message<UserDto> buildMessage(UserDto userDto) {
        return MessageBuilder.withPayload(userDto)
                .build();
    }

}
