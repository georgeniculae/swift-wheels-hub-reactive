package com.autohubreactive.agency.producer;

import com.autohubreactive.dto.agency.CarResponse;
import com.autohubreactive.lib.retry.RetryHandler;
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
public class CarAvailableProducerService {

    private final StreamBridge streamBridge;
    private final RetryHandler retryHandler;

    @Value("${spring.cloud.stream.bindings.carAvailableProducer-out-0.destination}")
    private String binderName;

    @Value("${spring.cloud.stream.bindings.carAvailableProducer-out-0.contentType}")
    private String mimeType;

    public Mono<Void> sendCarAvailable(CarResponse carResponse) {
        return Mono.fromRunnable(
                        () -> streamBridge.send(
                                binderName,
                                buildMessage(carResponse),
                                MimeType.valueOf(mimeType)
                        )
                )
                .subscribeOn(Schedulers.boundedElastic())
                .retryWhen(retryHandler.retry())
                .doOnSuccess(_ -> log.info("Car with id {} published to AI topic", carResponse.id()))
                .then();
    }

    private Message<CarResponse> buildMessage(CarResponse carResponse) {
        return MessageBuilder.withPayload(carResponse).build();
    }

}