package com.autohubreactive.booking.producer.dlq;

import com.autohubreactive.dto.booking.UpdatedBookingReprocessRequest;
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
public class FailedUpdatedBookingDlqProducerService {

    private final StreamBridge streamBridge;
    private final RetryHandler retryHandler;

    @Value("${spring.cloud.stream.bindings.failedUpdatedBookingDlqProducer-out-0.destination}")
    private String binderName;

    @Value("${spring.cloud.stream.bindings.failedUpdatedBookingDlqProducer-out-0.contentType}")
    private String mimeType;

    public Mono<Void> sendUpdatedBookingReprocessRequest(UpdatedBookingReprocessRequest updatedBookingReprocessRequest) {
        return Mono.fromRunnable(
                        () -> streamBridge.send(
                                binderName,
                                buildMessage(updatedBookingReprocessRequest),
                                MimeType.valueOf(mimeType)
                        )
                )
                .subscribeOn(Schedulers.boundedElastic())
                .retryWhen(retryHandler.retry())
                .then();
    }

    private Message<UpdatedBookingReprocessRequest> buildMessage(UpdatedBookingReprocessRequest updatedBookingReprocessRequest) {
        return MessageBuilder.withPayload(updatedBookingReprocessRequest)
                .build();
    }

}
