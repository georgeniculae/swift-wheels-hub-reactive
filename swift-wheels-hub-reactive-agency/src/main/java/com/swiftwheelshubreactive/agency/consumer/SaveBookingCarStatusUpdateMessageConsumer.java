package com.swiftwheelshubreactive.agency.consumer;

import com.swiftwheelshubreactive.agency.service.CarService;
import com.swiftwheelshubreactive.dto.CarStatusUpdate;
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
public class SaveBookingCarStatusUpdateMessageConsumer {

    private final CarService carService;
    private final RetryHandler retryHandler;

    @Bean
    public Function<Flux<Message<CarStatusUpdate>>, Mono<Void>> saveBookingCarUpdateConsumer() {
        return messageFlux -> messageFlux.concatMap(this::processCarUpdate)
                .then();
    }

    private Mono<Void> processCarUpdate(Message<CarStatusUpdate> message) {
        return carService.updateCarStatus(message.getPayload())
                .retryWhen(retryHandler.retry())
                .doOnSuccess(_ -> {
                    KafkaUtil.acknowledgeMessage(message.getHeaders());
                    log.info("Car status updated");
                })
                .onErrorResume(e -> {
                    log.error("Exception during car status update: {}", e.getMessage(), e);

                    return Mono.empty();
                });
    }

}
