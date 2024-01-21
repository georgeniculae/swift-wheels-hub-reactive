package com.swiftwheelshub.lib.config.kafka;

import com.swiftwheelshub.dto.UserDto;
import com.swiftwheelshub.lib.service.UserService;
import com.swiftwheelshub.lib.util.TestUtils;
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
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SavedUserMessageConsumerTest {

    @InjectMocks
    private SavedUserMessageConsumer savedUserMessageConsumer;

    @Mock
    private UserService userService;

    @Mock
    private Acknowledgment acknowledgment;

    @Test
    void userConsumerTest_success() {
        UserDto userDto = TestUtils.getResourceAsJson("/data/UserDto.json", UserDto.class);
        Message<UserDto> message = new GenericMessage<>(userDto);
        Flux<Message<UserDto>> flux = Flux.just(message);

        when(userService.processSavedUser(any(UserDto.class))).thenReturn(Mono.just(userDto));

        StepVerifier.create(savedUserMessageConsumer.savedUserConsumer().apply(flux))
                .expectComplete()
                .verify();
    }

    @Test
    void userConsumerTest_exceptionOnProcessMessage() {
        UserDto userDto = TestUtils.getResourceAsJson("/data/UserDto.json", UserDto.class);
        Message<UserDto> message = new GenericMessage<>(userDto);
        Flux<Message<UserDto>> flux = Flux.just(message);

        when(userService.processSavedUser(any(UserDto.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(savedUserMessageConsumer.savedUserConsumer().apply(flux))
                .expectComplete()
                .verify();
    }

    @Test
    void userConsumerTest_successAcknowledgedMessageWithHeaders() {
        ReflectionTestUtils.setField(savedUserMessageConsumer, "isMessageAckEnabled", true);

        UserDto userDto = TestUtils.getResourceAsJson("/data/UserDto.json", UserDto.class);
        MessageHeaders messageHeaders = new MessageHeaders(Map.of(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment));
        Message<UserDto> message = MessageBuilder.createMessage(userDto, messageHeaders);

        Flux<Message<UserDto>> flux = Flux.just(message);

        when(userService.processSavedUser(any(UserDto.class))).thenReturn(Mono.just(userDto));

        StepVerifier.create(savedUserMessageConsumer.savedUserConsumer().apply(flux))
                .expectComplete()
                .verify();
    }

    @Test
    void userConsumerTest_acknowledgedMessageWithoutHeaders() {
        ReflectionTestUtils.setField(savedUserMessageConsumer, "isMessageAckEnabled", true);

        UserDto userDto = TestUtils.getResourceAsJson("/data/UserDto.json", UserDto.class);
        Message<UserDto> message = new GenericMessage<>(userDto);
        Flux<Message<UserDto>> flux = Flux.just(message);

        when(userService.processSavedUser(any(UserDto.class))).thenReturn(Mono.just(userDto));

        StepVerifier.create(savedUserMessageConsumer.savedUserConsumer().apply(flux))
                .expectComplete()
                .verify();
    }

}
