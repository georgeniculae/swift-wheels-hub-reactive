package com.swiftwheelshubreactive.agency.consumer;

import com.swiftwheelshubreactive.agency.service.CarService;
import com.swiftwheelshubreactive.dto.UpdateCarsRequest;
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
public class CarUpdateAfterBookingUpdateMessageConsumer {

    private final CarService carService;
    private final RetryHandler retryHandler;

    @Bean
    public Function<Flux<Message<UpdateCarsRequest>>, Mono<Void>> carUpdateAfterBookingUpdateConsumer() {
        return messageFlux -> messageFlux.concatMap(this::processCarUpdate)
                .then();
    }

    private Mono<Void> processCarUpdate(Message<UpdateCarsRequest> message) {
        return carService.updateCarsStatus(message.getPayload())
                .retryWhen(retryHandler.retry())
                .doOnSuccess(_ -> {
                    KafkaUtil.acknowledgeMessage(message.getHeaders());

                    UpdateCarsRequest updateCarsRequest = message.getPayload();
                    log.info("Car: {} is available", updateCarsRequest.previousCarId());
                    log.info("Car: {} is not available", updateCarsRequest.actualCarId());
                })
                .onErrorResume(e -> {
                    log.error("Exception during car status update: {}", e.getMessage(), e);

                    return Mono.empty();
                });
    }

}
