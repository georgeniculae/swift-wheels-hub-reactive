package com.swiftwheelshubreactive.booking.service;

import com.swiftwheelshubreactive.dto.BookingResponse;
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
public class UpdatedBookingProducerService {

    @Value("${spring.cloud.stream.bindings.updatedBookingProducer-out-0.destination}")
    private String binderName;

    @Value("${spring.cloud.stream.bindings.updatedBookingProducer-out-0.contentType}")
    private String mimeType;

    private final StreamBridge streamBridge;

    public Mono<Boolean> sendMessage(BookingResponse bookingResponse) {
        return Mono.fromCallable(() -> streamBridge.send(binderName, buildMessage(bookingResponse), MimeType.valueOf(mimeType)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Message<BookingResponse> buildMessage(BookingResponse bookingResponse) {
        return MessageBuilder.withPayload(bookingResponse)
                .build();
    }

}
