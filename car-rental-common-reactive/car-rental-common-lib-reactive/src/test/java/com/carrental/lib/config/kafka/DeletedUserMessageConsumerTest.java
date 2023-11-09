package com.carrental.lib.config.kafka;

import com.carrental.lib.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeletedUserMessageConsumerTest {

    @InjectMocks
    private DeletedUserMessageConsumer deletedUserMessageConsumer;

    @Mock
    private UserService userService;

    @Test
    void userConsumerTest_success() {
        Message<String> message = new GenericMessage<>("user");
        Flux<Message<String>> flux = Flux.just(message);

        when(userService.processDeletedUser(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(deletedUserMessageConsumer.deletedUserConsumer().apply(flux))
                .expectComplete()
                .verify();
    }

    @Test
    void userConsumerTest_exceptionOnProcessMessage() {
        Message<String> message = new GenericMessage<>("user");
        Flux<Message<String>> flux = Flux.just(message);

        when(userService.processDeletedUser(anyString())).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(deletedUserMessageConsumer.deletedUserConsumer().apply(flux))
                .expectComplete()
                .verify();
    }

}
