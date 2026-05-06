package com.autohubreactive.ai.consumer;

import com.autohubreactive.ai.service.CarVectorSyncService;
import com.autohubreactive.dto.common.CarState;
import com.autohubreactive.dto.common.CarStatusUpdate;
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
public class CarStatusUpdateMessageConsumer {

    private final CarVectorSyncService carVectorSyncService;
    private final RetryHandler retryHandler;

    @Bean
    public Function<Flux<Message<CarStatusUpdate>>, Mono<Void>> carStatusUpdateConsumer() {
        return messageFlux -> messageFlux.concatMap(this::processMessage).then();
    }

    private Mono<Void> processMessage(Message<CarStatusUpdate> message) {
        CarStatusUpdate payload = message.getPayload();

        if (payload.carState() != CarState.NOT_AVAILABLE) {
            KafkaUtil.acknowledgeMessage(message.getHeaders());
            return Mono.empty();
        }

        return carVectorSyncService.removeCar(payload.carId())
                .retryWhen(retryHandler.retry())
                .doOnSuccess(_ -> {
                    KafkaUtil.acknowledgeMessage(message.getHeaders());
                    log.info("Car with id {} removed from vector store, state: {}",
                            payload.carId(), payload.carState().getDisplayName());
                })
                .onErrorResume(e -> {
                    log.error("Exception during car vector store update: {}", e.getMessage(), e);
                    
                    return Mono.empty();
                });
    }

}
