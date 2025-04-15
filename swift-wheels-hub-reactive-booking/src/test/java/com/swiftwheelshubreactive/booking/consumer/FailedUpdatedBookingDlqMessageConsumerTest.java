package com.swiftwheelshubreactive.booking.consumer;

import com.swiftwheelshubreactive.booking.service.dlq.UpdatedBookingReprocessService;
import com.swiftwheelshubreactive.booking.util.TestUtil;
import com.swiftwheelshubreactive.dto.UpdatedBookingReprocessRequest;
import com.swiftwheelshubreactive.lib.retry.RetryHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.retry.RetrySpec;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FailedUpdatedBookingDlqMessageConsumerTest {

    @InjectMocks
    private FailedUpdatedBookingDlqMessageConsumer failedUpdatedBookingDlqMessageConsumer;

    @Mock
    private UpdatedBookingReprocessService updatedBookingReprocessService;

    @Mock
    private Acknowledgment acknowledgment;

    @Mock
    private RetryHandler retryHandler;

    @Test
    void failedUpdatedBookingDlqConsumerTest_success() {
        UpdatedBookingReprocessRequest updatedBookingReprocessRequest =
                TestUtil.getResourceAsJson("/data/UpdatedBookingReprocessRequest.json", UpdatedBookingReprocessRequest.class);

        Message<UpdatedBookingReprocessRequest> message = MessageBuilder.withPayload(updatedBookingReprocessRequest)
                .setHeader(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment)
                .build();

        Flux<Message<UpdatedBookingReprocessRequest>> messageFlux = Flux.just(message);

        when(updatedBookingReprocessService.reprocessUpdatedBooking(any(UpdatedBookingReprocessRequest.class)))
                .thenReturn(Mono.empty());
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ZERO));

        failedUpdatedBookingDlqMessageConsumer.failedUpdatedBookingDlqConsumer().apply(messageFlux)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void failedUpdatedBookingDlqConsumerTest_noAcknowledgement() {
        UpdatedBookingReprocessRequest createdBookingReprocessRequest =
                TestUtil.getResourceAsJson("/data/UpdatedBookingReprocessRequest.json", UpdatedBookingReprocessRequest.class);

        Message<UpdatedBookingReprocessRequest> message = MessageBuilder.withPayload(createdBookingReprocessRequest)
                .build();

        Flux<Message<UpdatedBookingReprocessRequest>> messageFlux = Flux.just(message);

        when(updatedBookingReprocessService.reprocessUpdatedBooking(any(UpdatedBookingReprocessRequest.class)))
                .thenReturn(Mono.empty());
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ZERO));

        failedUpdatedBookingDlqMessageConsumer.failedUpdatedBookingDlqConsumer().apply(messageFlux)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

}
