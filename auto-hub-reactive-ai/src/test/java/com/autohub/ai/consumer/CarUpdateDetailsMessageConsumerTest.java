package com.autohub.ai.consumer;

import com.autohubreactive.ai.consumer.CarUpdateDetailsMessageConsumer;
import com.autohubreactive.ai.service.CarVectorSyncService;
import com.autohubreactive.dto.common.CarState;
import com.autohubreactive.dto.common.CarUpdateDetails;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarUpdateDetailsMessageConsumerTest {

    @InjectMocks
    private CarUpdateDetailsMessageConsumer carUpdateDetailsMessageConsumer;

    @Mock
    private CarVectorSyncService carVectorSyncService;

    @Mock
    private Acknowledgment acknowledgment;

    @Mock
    private RetryHandler retryHandler;

    @Test
    void carUpdateDetailsConsumerTest_notAvailable_success() {
        CarUpdateDetails carUpdateDetails = CarUpdateDetails.builder()
                .carId("64f361caf291ae086e179547")
                .carState(CarState.NOT_AVAILABLE)
                .receptionistEmployeeId("64f361caf291ae086e179547")
                .build();

        Message<CarUpdateDetails> message = MessageBuilder.withPayload(carUpdateDetails)
                .setHeader(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment)
                .build();

        when(carVectorSyncService.removeCar(anyString())).thenReturn(Mono.empty());
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofMinutes(0)));

        carUpdateDetailsMessageConsumer.carUpdateDetailsConsumer().apply(Flux.just(message))
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void carUpdateDetailsConsumerTest_notAvailable_noAcknowledgement() {
        CarUpdateDetails carUpdateDetails = CarUpdateDetails.builder()
                .carId("64f361caf291ae086e179547")
                .carState(CarState.NOT_AVAILABLE)
                .receptionistEmployeeId("64f361caf291ae086e179547")
                .build();

        Message<CarUpdateDetails> message = MessageBuilder.withPayload(carUpdateDetails).build();

        when(carVectorSyncService.removeCar(anyString())).thenReturn(Mono.empty());
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofMinutes(0)));

        carUpdateDetailsMessageConsumer.carUpdateDetailsConsumer().apply(Flux.just(message))
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void carUpdateDetailsConsumerTest_available_skipsVectorStore() {
        CarUpdateDetails carUpdateDetails = CarUpdateDetails.builder()
                .carId("64f361caf291ae086e179547")
                .carState(CarState.AVAILABLE)
                .receptionistEmployeeId("64f361caf291ae086e179547")
                .build();

        Message<CarUpdateDetails> message = MessageBuilder.withPayload(carUpdateDetails)
                .setHeader(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment)
                .build();

        carUpdateDetailsMessageConsumer.carUpdateDetailsConsumer().apply(Flux.just(message))
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        verifyNoInteractions(carVectorSyncService);
    }

}