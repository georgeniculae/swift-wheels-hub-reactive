package com.swiftwheelshubreactive.booking.producer.bookingprocessing;

import com.swiftwheelshubreactive.dto.BookingResponse;
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
public class UpdatedBookingProducerService {

    private final StreamBridge streamBridge;

    @Value("${spring.cloud.stream.bindings.updatedBookingProducer-out-0.destination}")
    private String binderName;

    @Value("${spring.cloud.stream.bindings.updatedBookingProducer-out-0.contentType}")
    private String mimeType;

    public Mono<Void> sendBookingResponse(BookingResponse bookingResponse) {
        return Mono.fromCallable(
                        () -> streamBridge.send(
                                binderName,
                                buildMessage(bookingResponse),
                                MimeType.valueOf(mimeType)
                        )
                )
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    private Message<BookingResponse> buildMessage(BookingResponse bookingResponse) {
        return MessageBuilder.withPayload(bookingResponse)
                .build();
    }

}
