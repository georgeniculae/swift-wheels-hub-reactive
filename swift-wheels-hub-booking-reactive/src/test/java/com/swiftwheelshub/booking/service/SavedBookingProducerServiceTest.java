package com.swiftwheelshub.booking.service;

import com.swiftwheelshub.booking.util.TestUtils;
import com.carrental.dto.BookingDto;
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
class SavedBookingProducerServiceTest {

    @InjectMocks
    private SavedBookingProducerService savedBookingProducerService;

    @Mock
    private StreamBridge streamBridge;

    @Test
    void sendMessageTest() {
        ReflectionTestUtils.setField(savedBookingProducerService, "binderName", "booking-producer-out-0");
        ReflectionTestUtils.setField(savedBookingProducerService, "mimeType", "application/json");

        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        when(streamBridge.send(anyString(), any(Message.class), any(MimeType.class))).thenReturn(true);

        StepVerifier.create(savedBookingProducerService.sendMessage(bookingDto))
                .expectNext(true)
                .verifyComplete();
    }

}
