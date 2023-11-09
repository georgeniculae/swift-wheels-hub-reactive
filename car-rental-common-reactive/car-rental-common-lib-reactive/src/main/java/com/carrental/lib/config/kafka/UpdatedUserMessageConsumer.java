package com.carrental.lib.config.kafka;

import com.carrental.dto.UserDto;
import com.carrental.lib.exceptionhandling.CarRentalResponseStatusException;
import com.carrental.lib.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ConditionalOnProperty(prefix = "userConsumer", name = "enabled")
@Slf4j
public class UpdatedUserMessageConsumer {

    @Value("${userConsumer.isMessageAckEnabled:false}")
    private boolean isMessageAckEnabled;

    private final UserService userService;

    @Bean
    public Function<Flux<Message<UserDto>>, Mono<Void>> updatedUserConsumer() {
        return messageFlux -> messageFlux.concatMap(this::consumeMessage)
                .then();
    }

    private Mono<UserDto> consumeMessage(Message<UserDto> message) {
        return Mono.just(message.getPayload())
                .flatMap(userService::processUpdatedUser)
                .doOnNext(userDto -> {
                    log.info("User saved: {}", userDto);

                    if (isMessageAckEnabled) {
                        this.sendMessageAcknowledgement(message.getHeaders());
                    }
                })
                .onErrorResume(e -> {
                    log.error("Exception during processing user message: {}", e.getMessage(), e);

                    return Mono.empty();
                });
    }

    private void sendMessageAcknowledgement(MessageHeaders messageHeaders) {
        Optional.ofNullable(messageHeaders.get(KafkaHeaders.ACKNOWLEDGMENT, Acknowledgment.class))
                .orElseThrow(
                        () -> new CarRentalResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "There is no Kafka acknowledgement in message headers"
                        )
                )
                .acknowledge();
    }

}
