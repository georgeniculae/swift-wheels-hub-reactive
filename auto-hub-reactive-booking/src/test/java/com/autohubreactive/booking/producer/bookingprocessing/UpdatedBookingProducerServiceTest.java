package com.autohubreactive.booking.producer.bookingprocessing;

import com.autohubreactive.booking.util.TestUtil;
import com.autohubreactive.dto.common.BookingResponse;
import com.autohubreactive.lib.retry.RetryHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.MimeType;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdatedBookingProducerServiceTest {

    @InjectMocks
    private UpdatedBookingProducerService updatedBookingProducerService;

    @Mock
    private StreamBridge streamBridge;

    @Mock
    private RetryHandler retryHandler;

    @Test
    void sendBookingResponseTest() {
        ReflectionTestUtils.setField(updatedBookingProducerService, "binderName", "booking-producer-out-0");
        ReflectionTestUtils.setField(updatedBookingProducerService, "mimeType", "application/json");

        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        when(streamBridge.send(anyString(), any(Message.class), any(MimeType.class))).thenReturn(true);
        when(retryHandler.retry()).thenReturn(Retry.backoff(0, Duration.ZERO));

        updatedBookingProducerService.sendBookingResponse(bookingResponse)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

}
