package com.autohubreactive.agency.consumer;

import com.autohubreactive.agency.service.CarService;
import com.autohubreactive.dto.CarUpdateDetails;
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
public class UpdatedCarInvoiceClosedMessageConsumer {

    private final CarService carService;
    private final RetryHandler retryHandler;

    @Bean
    public Function<Flux<Message<CarUpdateDetails>>, Mono<Void>> updatedCarInvoiceClosedConsumer() {
        return messageFlux -> messageFlux.concatMap(this::processCarUpdate)
                .then();
    }

    private Mono<Void> processCarUpdate(Message<CarUpdateDetails> message) {
        return carService.updateCarWhenBookingIsClosed(message.getPayload())
                .retryWhen(retryHandler.retry())
                .doOnSuccess(_ -> {
                    KafkaUtil.acknowledgeMessage(message.getHeaders());
                    log.info("Status updated for car: {} after closing booking", message.getPayload().carId());
                })
                .onErrorResume(e -> {
                    log.error("Exception during car status update: {}", e.getMessage(), e);

                    return Mono.empty();
                });
    }

}
