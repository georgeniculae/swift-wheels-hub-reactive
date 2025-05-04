package com.autohubreactive.booking.producer.bookingprocessing;

import com.autohubreactive.dto.BookingResponse;
import com.autohubreactive.lib.retry.RetryHandler;
import lombok.RequiredArgsConstructor;
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
public class CreatedBookingProducerService {

    private final StreamBridge streamBridge;
    private final RetryHandler retryHandler;

    @Value("${spring.cloud.stream.bindings.savedBookingProducer-out-0.destination}")
    private String binderName;

    @Value("${spring.cloud.stream.bindings.savedBookingProducer-out-0.contentType}")
    private String mimeType;

    public Mono<Void> sendCreatedBooking(BookingResponse bookingResponse) {
        return Mono.fromRunnable(
                        () -> streamBridge.send(
                                binderName,
                                buildMessage(bookingResponse),
                                MimeType.valueOf(mimeType)
                        )
                )
                .subscribeOn(Schedulers.boundedElastic())
                .retryWhen(retryHandler.retry())
                .then();
    }

    private Message<BookingResponse> buildMessage(BookingResponse bookingResponse) {
        return MessageBuilder.withPayload(bookingResponse)
                .build();
    }

}
