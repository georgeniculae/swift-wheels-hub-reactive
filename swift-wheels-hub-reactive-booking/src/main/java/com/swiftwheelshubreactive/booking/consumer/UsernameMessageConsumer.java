package com.swiftwheelshubreactive.booking.consumer;

import com.swiftwheelshubreactive.booking.service.bookingprocessing.BookingService;
import com.swiftwheelshubreactive.lib.retry.RetryHandler;
import com.swiftwheelshubreactive.lib.util.KafkaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class UsernameMessageConsumer {

    private final BookingService bookingService;
    private final RetryHandler retryHandler;

    @Bean
    public Function<Flux<Message<String>>, Mono<Void>> usernameConsumer() {
        return messageFlux -> messageFlux.concatMap(this::processBookingDeletion)
                .then();
    }

    private Mono<Void> processBookingDeletion(Message<String> message) {
        return bookingService.deleteBookingByCustomerUsername(message.getPayload())
                .retryWhen(retryHandler.retry())
                .doOnSuccess(_ -> {
                    KafkaUtil.acknowledgeMessage(message.getHeaders());
                    log.info("Booking delete for username {}", message.getPayload());
                })
                .onErrorResume(e -> {
                    log.error("Exception during processing saved booking message: {}", e.getMessage(), e);

                    return Mono.empty();
                });
    }

}
