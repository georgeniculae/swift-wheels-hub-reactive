package com.autohubreactive.booking.consumer;

import com.autohubreactive.booking.service.dlq.CreatedBookingReprocessService;
import com.autohubreactive.dto.CreatedBookingReprocessRequest;
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
public class FailedCreatedBookingDlqMessageConsumer {

    private final CreatedBookingReprocessService createdBookingReprocessService;
    private final RetryHandler retryHandler;

    @Bean
    public Function<Flux<Message<CreatedBookingReprocessRequest>>, Mono<Void>> failedCreatedBookingDlqConsumer() {
        return messageFlux -> messageFlux.concatMap(this::reprocessUpdatedBooking)
                .then();
    }

    public Mono<Void> reprocessUpdatedBooking(Message<CreatedBookingReprocessRequest> message) {
        return createdBookingReprocessService.reprocessCreatedBooking(message.getPayload())
                .retryWhen(retryHandler.retry())
                .doOnSuccess(_ -> {
                    KafkaUtil.acknowledgeMessage(message.getHeaders());
                    log.info("Booking: {} reprocessed successfully", message.getPayload().id());
                })
                .onErrorResume(e -> {
                    log.info("Error while reprocessing invoice: {}", e.getMessage());

                    return Mono.empty();
                });
    }

}
