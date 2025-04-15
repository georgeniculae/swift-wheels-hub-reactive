package com.swiftwheelshubreactive.booking.producer.bookingprocessing;

import com.swiftwheelshubreactive.booking.util.TestUtil;
import com.swiftwheelshubreactive.dto.BookingResponse;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdatedBookingProducerServiceTest {

    @InjectMocks
    private UpdatedBookingProducerService updatedBookingProducerService;

    @Mock
    private StreamBridge streamBridge;

    @Test
    void sengBookingResponseTest() {
        ReflectionTestUtils.setField(updatedBookingProducerService, "binderName", "booking-producer-out-0");
        ReflectionTestUtils.setField(updatedBookingProducerService, "mimeType", "application/json");

        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        when(streamBridge.send(anyString(), any(Message.class), any(MimeType.class))).thenReturn(true);

        StepVerifier.create(updatedBookingProducerService.sengBookingResponse(bookingResponse))
                .expectNext(true)
                .verifyComplete();
    }

}
