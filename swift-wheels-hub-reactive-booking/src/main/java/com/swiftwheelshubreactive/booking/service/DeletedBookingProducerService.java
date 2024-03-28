package com.swiftwheelshubreactive.booking.service;

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
public class DeletedBookingProducerService {

    @Value("${spring.cloud.stream.bindings.deletedBookingProducer-out-0.destination}")
    private String binderName;

    @Value("${spring.cloud.stream.bindings.deletedBookingProducer-out-0.contentType}")
    private String mimeType;

    private final StreamBridge streamBridge;

    public Mono<Boolean> sendMessage(String bookingId) {
        return Mono.fromCallable(() -> streamBridge.send(binderName, buildMessage(bookingId), MimeType.valueOf(mimeType)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Message<String> buildMessage(String bookingId) {
        return MessageBuilder.withPayload(bookingId)
                .build();
    }

}
