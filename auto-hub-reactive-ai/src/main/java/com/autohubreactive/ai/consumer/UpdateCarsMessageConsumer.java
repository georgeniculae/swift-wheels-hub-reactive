package com.autohubreactive.ai.consumer;

import com.autohubreactive.ai.service.CarVectorSyncService;
import com.autohubreactive.dto.common.UpdateCarsRequest;
import com.autohubreactive.lib.retry.RetryHandler;
import com.autohubreactive.lib.util.KafkaUtil;
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
public class UpdateCarsMessageConsumer {

    private final CarVectorSyncService carVectorSyncService;
    private final RetryHandler retryHandler;

    @Bean
    public Function<Flux<Message<UpdateCarsRequest>>, Mono<Void>> updateCarsConsumer() {
        return messageFlux -> messageFlux.concatMap(this::processMessage).then();
    }

    private Mono<Void> processMessage(Message<UpdateCarsRequest> message) {
        UpdateCarsRequest payload = message.getPayload();

        return carVectorSyncService.removeCar(payload.actualCarId())
                .retryWhen(retryHandler.retry())
                .doOnSuccess(_ -> {
                    KafkaUtil.acknowledgeMessage(message.getHeaders());
                    log.info("Car {} removed from vector store", payload.actualCarId());
                })
                .onErrorResume(e -> {
                    log.error("Exception during cars vector store update: {}", e.getMessage(), e);
                    return Mono.empty();
                });
    }

}
