package com.swiftwheelshubreactive.booking.producer;

import com.swiftwheelshubreactive.dto.CarStatusUpdate;
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
public class CreateBookingCarUpdateProducerService {

    private final StreamBridge streamBridge;
    private final RetryHandler retryHandler;

    @Value("${spring.cloud.stream.bindings.saveBookingCarUpdateProducer-out-0.destination}")
    private String binderName;

    @Value("${spring.cloud.stream.bindings.saveBookingCarUpdateProducer-out-0.contentType}")
    private String mimeType;

    public Mono<Boolean> sendCarUpdateDetails(CarStatusUpdate carStatusUpdate) {
        return Mono.fromCallable(
                        () -> streamBridge.send(
                                binderName,
                                buildMessage(carStatusUpdate),
                                MimeType.valueOf(mimeType)
                        )
                )
                .subscribeOn(Schedulers.boundedElastic())
                .retryWhen(retryHandler.retry());
    }

    private Message<CarStatusUpdate> buildMessage(CarStatusUpdate carStatusUpdate) {
        return MessageBuilder.withPayload(carStatusUpdate)
                .build();
    }

}
