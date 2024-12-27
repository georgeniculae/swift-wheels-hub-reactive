package com.swiftwheelshubreactive.booking.consumer;

import com.swiftwheelshubreactive.booking.service.BookingService;
import com.swiftwheelshubreactive.dto.BookingClosingDetails;
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
public class BookingUpdateMessageConsumer {

    private final BookingService bookingService;
    private final RetryHandler retryHandler;

    @Bean
    public Function<Flux<Message<BookingClosingDetails>>, Mono<Void>> bookingUpdateConsumer() {
        return messageFlux -> messageFlux.concatMap(this::processBookingClosing)
                .then();
    }

    private Mono<Void> processBookingClosing(Message<BookingClosingDetails> message) {
        return bookingService.closeBooking(message.getPayload())
                .retryWhen(retryHandler.retry())
                .doOnSuccess(_ -> {
                    KafkaUtil.acknowledgeMessage(message.getHeaders());
                    log.info("Booking closed");
                })
                .onErrorResume(e -> {
                    log.error("Exception during processing saved booking message: {}", e.getMessage(), e);

                    return Mono.empty();
                });
    }

}
