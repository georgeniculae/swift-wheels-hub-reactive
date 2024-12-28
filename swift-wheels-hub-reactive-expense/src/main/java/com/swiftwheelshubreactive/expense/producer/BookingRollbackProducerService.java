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
    private String emailNotificationBinderName;

    @Value("${spring.cloud.stream.bindings.bookingRollbackProducer-out-0.contentType}")
    private String emailNotificationMimeType;

    public Mono<Boolean> sendBookingId(String bookingId) {
        return Mono.fromCallable(
                        () -> streamBridge.send(
                                emailNotificationBinderName,
                                buildMessage(bookingId),
                                MimeType.valueOf(emailNotificationMimeType)
                        )
                )
                .subscribeOn(Schedulers.boundedElastic())
                .filter(Boolean.TRUE::equals)
                .switchIfEmpty(Mono.error(new SwiftWheelsHubException("Failed to send booking id for rollback")))
                .retryWhen(retryHandler.retry())
                .onErrorResume(e -> {
                    log.error(
                            "Error while sending booking id for rollback: {}, saving message on rollback DLQ",
                            e.getMessage()
                    );

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    private Message<String> buildMessage(String bookingId) {
        return MessageBuilder.withPayload(bookingId)
                .build();
    }

}
