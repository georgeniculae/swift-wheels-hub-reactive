package com.swiftwheelshubreactive.expense.producer;

import com.swiftwheelshubreactive.lib.retry.RetryHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.MimeType;
import reactor.test.StepVerifier;
import reactor.util.retry.RetrySpec;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingRollbackProducerServiceTest {

    @InjectMocks
    private BookingRollbackProducerService bookingRollbackProducerService;

    @Mock
    private StreamBridge streamBridge;

    @Mock
    private RetryHandler retryHandler;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(bookingRollbackProducerService, "binderName", "bookingRollbackProducer");
        ReflectionTestUtils.setField(bookingRollbackProducerService, "mimeType", "application/json");
    }

    @Test
    void sendBookingIdTest_success() {
        when(streamBridge.send(anyString(), any(Object.class), any(MimeType.class))).thenReturn(true);
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofMinutes(0)));

        bookingRollbackProducerService.sendBookingId("id")
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();
    }

}