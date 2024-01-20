package com.swiftwheelshub.lib.config.kafka;

import com.swiftwheelshub.lib.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "userConsumer", name = "enabled")
@Slf4j
public class DeletedUserMessageConsumer {

    private final UserService userService;

    @Bean
    public Function<Flux<Message<String>>, Mono<Void>> deletedUserConsumer() {
        return messageFlux -> messageFlux.concatMap(this::consumeMessage)
                .then();
    }

    private Mono<Void> consumeMessage(Message<String> message) {
        return Mono.just(message.getPayload())
                .flatMap(userService::processDeletedUser)
                .onErrorResume(e -> {
                    log.error("Exception during processing user message: {}", e.getMessage(), e);

                    return Mono.empty();
                });
    }

}
