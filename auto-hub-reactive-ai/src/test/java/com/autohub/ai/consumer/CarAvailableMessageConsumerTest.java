package com.autohub.ai.consumer;

import com.autohub.ai.util.TestUtil;
import com.autohubreactive.ai.consumer.CarAvailableMessageConsumer;
import com.autohubreactive.ai.service.CarVectorStoreService;
import com.autohubreactive.dto.ai.AvailableCarDetails;
import com.autohubreactive.lib.retry.RetryHandler;
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
class CarAvailableMessageConsumerTest {

    @InjectMocks
    private CarAvailableMessageConsumer carAvailableMessageConsumer;

    @Mock
    private CarVectorStoreService carVectorStoreService;

    @Mock
    private Acknowledgment acknowledgment;

    @Mock
    private RetryHandler retryHandler;

    @Test
    void carAvailableConsumerTest_success() {
        AvailableCarDetails availableCarDetails =
                TestUtil.getResourceAsJson("/data/AvailableCarDetails.json", AvailableCarDetails.class);

        Message<AvailableCarDetails> message = MessageBuilder.withPayload(availableCarDetails)
                .setHeader(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment)
                .build();

        when(carVectorStoreService.addCar(any(AvailableCarDetails.class))).thenReturn(Mono.empty());
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofMinutes(0)));

        carAvailableMessageConsumer.carAvailableConsumer().apply(Flux.just(message))
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void carAvailableConsumerTest_noAcknowledgement() {
        AvailableCarDetails availableCarDetails =
                TestUtil.getResourceAsJson("/data/AvailableCarDetails.json", AvailableCarDetails.class);

        Message<AvailableCarDetails> message = MessageBuilder.withPayload(availableCarDetails).build();

        when(carVectorStoreService.addCar(any(AvailableCarDetails.class))).thenReturn(Mono.empty());
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofMinutes(0)));

        carAvailableMessageConsumer.carAvailableConsumer().apply(Flux.just(message))
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

}