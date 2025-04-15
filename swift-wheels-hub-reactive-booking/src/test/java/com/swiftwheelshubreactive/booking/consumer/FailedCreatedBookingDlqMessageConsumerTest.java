package com.swiftwheelshubreactive.booking.consumer;

import com.swiftwheelshubreactive.booking.service.dlq.CreatedBookingReprocessService;
import com.swiftwheelshubreactive.booking.util.TestUtil;
import com.swiftwheelshubreactive.dto.CreatedBookingReprocessRequest;
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
class FailedCreatedBookingDlqMessageConsumerTest {

    @InjectMocks
    private FailedCreatedBookingDlqMessageConsumer failedCreatedBookingDlqMessageConsumer;

    @Mock
    private CreatedBookingReprocessService createdBookingReprocessService;

    @Mock
    private Acknowledgment acknowledgment;

    @Mock
    private RetryHandler retryHandler;

    @Test
    void failedCreatedBookingDlqConsumerTest_success() {
        CreatedBookingReprocessRequest createdBookingReprocessRequest =
                TestUtil.getResourceAsJson("/data/CreatedBookingReprocessRequest.json", CreatedBookingReprocessRequest.class);

        Message<CreatedBookingReprocessRequest> message = MessageBuilder.withPayload(createdBookingReprocessRequest)
                .setHeader(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment)
                .build();

        Flux<Message<CreatedBookingReprocessRequest>> messageFlux = Flux.just(message);

        when(createdBookingReprocessService.reprocessCreatedBooking(any(CreatedBookingReprocessRequest.class)))
                .thenReturn(Mono.empty());
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ZERO));

        failedCreatedBookingDlqMessageConsumer.failedCreatedBookingDlqConsumer().apply(messageFlux)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void failedCreatedBookingDlqConsumerTest_noAcknowledgement() {
        CreatedBookingReprocessRequest createdBookingReprocessRequest =
                TestUtil.getResourceAsJson("/data/CreatedBookingReprocessRequest.json", CreatedBookingReprocessRequest.class);

        Message<CreatedBookingReprocessRequest> message = MessageBuilder.withPayload(createdBookingReprocessRequest)
                .build();

        Flux<Message<CreatedBookingReprocessRequest>> messageFlux = Flux.just(message);

        when(createdBookingReprocessService.reprocessCreatedBooking(any(CreatedBookingReprocessRequest.class)))
                .thenReturn(Mono.empty());
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ZERO));

        failedCreatedBookingDlqMessageConsumer.failedCreatedBookingDlqConsumer().apply(messageFlux)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

}
