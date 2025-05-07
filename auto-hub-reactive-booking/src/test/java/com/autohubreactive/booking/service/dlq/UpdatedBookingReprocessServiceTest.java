package com.autohubreactive.booking.service.dlq;

import com.autohubreactive.booking.mapper.BookingMapper;
import com.autohubreactive.booking.mapper.BookingMapperImpl;
import com.autohubreactive.booking.producer.bookingprocessing.UpdatedBookingProducerService;
import com.autohubreactive.booking.producer.bookingprocessing.UpdatedBookingUpdateCarsProducerService;
import com.autohubreactive.booking.util.TestUtil;
import com.autohubreactive.dto.booking.UpdatedBookingReprocessRequest;
import com.autohubreactive.dto.common.BookingResponse;
import com.autohubreactive.dto.common.UpdateCarsRequest;
import com.autohubreactive.lib.retry.RetryHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdatedBookingReprocessServiceTest {

    @InjectMocks
    private UpdatedBookingReprocessService updatedBookingReprocessService;

    @Mock
    private UpdatedBookingProducerService updatedBookingProducerService;

    @Mock
    private UpdatedBookingUpdateCarsProducerService updatedBookingUpdateCarsProducerService;

    @Mock
    private RetryHandler retryHandler;

    @Spy
    private BookingMapper bookingMapper = new BookingMapperImpl();

    @Test
    void reprocessUpdatedBookingTest_success() {
        UpdatedBookingReprocessRequest updatedBookingReprocessRequest =
                TestUtil.getResourceAsJson("/data/UpdatedBookingReprocessRequestCarChanged.json", UpdatedBookingReprocessRequest.class);

        when(updatedBookingUpdateCarsProducerService.sendUpdateCarsRequest(any(UpdateCarsRequest.class)))
                .thenReturn(Mono.empty());
        when(updatedBookingProducerService.sendBookingResponse(any(BookingResponse.class)))
                .thenReturn(Mono.empty());
        when(retryHandler.retry()).thenReturn(Retry.fixedDelay(0, Duration.ZERO));

        updatedBookingReprocessService.reprocessUpdatedBooking(updatedBookingReprocessRequest)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        verify(bookingMapper).mapToBookingResponse(any(UpdatedBookingReprocessRequest.class));
    }

    @Test
    void reprocessUpdatedBookingTest_carUpdateSendFailed() {
        UpdatedBookingReprocessRequest updatedBookingReprocessRequest =
                TestUtil.getResourceAsJson("/data/UpdatedBookingReprocessRequestCarChanged.json", UpdatedBookingReprocessRequest.class);

        when(updatedBookingUpdateCarsProducerService.sendUpdateCarsRequest(any(UpdateCarsRequest.class)))
                .thenReturn(Mono.error(new RuntimeException("Test")));
        when(retryHandler.retry()).thenReturn(Retry.fixedDelay(0, Duration.ZERO));

        updatedBookingReprocessService.reprocessUpdatedBooking(updatedBookingReprocessRequest)
                .as(StepVerifier::create)
                .expectError()
                .verify();

        verify(updatedBookingProducerService, never()).sendBookingResponse(any(BookingResponse.class));
    }

    @Test
    void reprocessUpdatedBookingTest_errorOnBookingSend() {
        UpdatedBookingReprocessRequest updatedBookingReprocessRequest =
                TestUtil.getResourceAsJson("/data/UpdatedBookingReprocessRequestCarChanged.json", UpdatedBookingReprocessRequest.class);

        when(updatedBookingUpdateCarsProducerService.sendUpdateCarsRequest(any(UpdateCarsRequest.class)))
                .thenReturn(Mono.error(new RuntimeException("est")));
        when(retryHandler.retry()).thenReturn(Retry.fixedDelay(0, Duration.ZERO));

        updatedBookingReprocessService.reprocessUpdatedBooking(updatedBookingReprocessRequest)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

}
