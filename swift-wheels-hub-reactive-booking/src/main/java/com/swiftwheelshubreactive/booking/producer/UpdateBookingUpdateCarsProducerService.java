package com.swiftwheelshubreactive.booking.producer;

import com.swiftwheelshubreactive.dto.UpdateCarsRequest;
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
public class UpdateBookingUpdateCarsProducerService {

    private final StreamBridge streamBridge;
    private final RetryHandler retryHandler;

    @Value("${spring.cloud.stream.bindings.updateBookingCarsUpdateProducer-out-0.destination}")
    private String binderName;

    @Value("${spring.cloud.stream.bindings.updateBookingCarsUpdateProducer-out-0.contentType}")
    private String mimeType;

    public Mono<Boolean> sendUpdateCarsRequest(UpdateCarsRequest updateCarsRequest) {
        return Mono.fromCallable(
                        () -> streamBridge.send(
                                binderName,
                                buildMessage(updateCarsRequest),
                                MimeType.valueOf(mimeType)
                        )
                )
                .subscribeOn(Schedulers.boundedElastic())
                .retryWhen(retryHandler.retry());
    }

    private Message<UpdateCarsRequest> buildMessage(UpdateCarsRequest updateCarsRequest) {
        return MessageBuilder.withPayload(updateCarsRequest)
                .build();
    }

}