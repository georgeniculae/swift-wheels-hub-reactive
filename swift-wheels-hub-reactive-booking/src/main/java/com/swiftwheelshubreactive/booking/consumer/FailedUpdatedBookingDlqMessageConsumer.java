package com.swiftwheelshubreactive.booking.consumer;

import com.swiftwheelshubreactive.booking.service.dlq.UpdatedBookingReprocessService;
import com.swiftwheelshubreactive.dto.UpdatedBookingReprocessRequest;
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
public class FailedUpdatedBookingDlqMessageConsumer {

    private final UpdatedBookingReprocessService updatedBookingReprocessService;
    private final RetryHandler retryHandler;

    @Bean
    public Function<Flux<Message<UpdatedBookingReprocessRequest>>, Mono<Void>> failedUpdatedBookingDlqConsumer() {
        return messageFlux -> messageFlux.concatMap(this::reprocessUpdatedBooking)
                .then();
    }

    public Mono<Void> reprocessUpdatedBooking(Message<UpdatedBookingReprocessRequest> message) {
        return updatedBookingReprocessService.reprocessUpdatedBooking(message.getPayload())
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
