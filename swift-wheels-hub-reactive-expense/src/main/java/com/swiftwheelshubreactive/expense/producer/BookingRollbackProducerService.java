package com.swiftwheelshubreactive.expense.producer;

import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import com.swiftwheelshubreactive.lib.retry.RetryHandler;
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
public class BookingRollbackProducerService {

    private final StreamBridge streamBridge;
    private final RetryHandler retryHandler;

    @Value("${spring.cloud.stream.bindings.bookingRollbackProducer-out-0.destination}")
    private String binderName;

    @Value("${spring.cloud.stream.bindings.bookingRollbackProducer-out-0.contentType}")
    private String mimeType;

    public Mono<Boolean> sendBookingId(String bookingId) {
        return Mono.fromCallable(
                        () -> streamBridge.send(
                                binderName,
                                buildMessage(bookingId),
                                MimeType.valueOf(mimeType)
                        )
                )
                .subscribeOn(Schedulers.boundedElastic())
                .filter(Boolean.TRUE::equals)
                .switchIfEmpty(Mono.error(new SwiftWheelsHubException("Booking rollback failed")))
                .retryWhen(retryHandler.retry());
    }

    private Message<String> buildMessage(String bookingId) {
        return MessageBuilder.withPayload(bookingId)
                .build();
    }

}
