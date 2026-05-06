package com.autohubreactive.ai.consumer;

import com.autohubreactive.ai.service.CarVectorStoreService;
import com.autohubreactive.dto.agency.CarResponse;
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
public class CarAvailableMessageConsumer {

    private final CarVectorStoreService carVectorStoreService;
    private final RetryHandler retryHandler;

    @Bean
    public Function<Flux<Message<CarResponse>>, Mono<Void>> carAvailableConsumer() {
        return messageFlux -> messageFlux.concatMap(this::processMessage).then();
    }

    private Mono<Void> processMessage(Message<CarResponse> message) {
        return carVectorStoreService.addCar(message.getPayload())
                .retryWhen(retryHandler.retry())
                .doOnSuccess(_ -> {
                    KafkaUtil.acknowledgeMessage(message.getHeaders());
                    log.info("Car with id {} added to vector store", message.getPayload().id());
                })
                .onErrorResume(e -> {
                    log.error("Exception adding car to vector store: {}", e.getMessage(), e);
                    return Mono.empty();
                });
    }

}