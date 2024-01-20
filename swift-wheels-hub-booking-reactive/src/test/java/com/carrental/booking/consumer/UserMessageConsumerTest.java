package com.carrental.booking.consumer;

import com.carrental.booking.service.BookingService;
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
class UserMessageConsumerTest {

    @InjectMocks
    private UserMessageConsumer userMessageConsumer;

    @Mock
    private BookingService bookingService;

    @Test
    void userConsumerTest_success() {
        Message<String> message = new GenericMessage<>("user");
        Flux<Message<String>> flux = Flux.just(message);

        when(bookingService.processBookingDeletion(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(userMessageConsumer.deletedUserConsumer().apply(flux))
                .expectComplete()
                .verify();
    }

    @Test
    void userConsumerTest_exceptionOnProcessMessage() {
        Message<String> message = new GenericMessage<>("user");
        Flux<Message<String>> flux = Flux.just(message);

        when(bookingService.processBookingDeletion(anyString())).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(userMessageConsumer.deletedUserConsumer().apply(flux))
                .expectComplete()
                .verify();
    }

}
