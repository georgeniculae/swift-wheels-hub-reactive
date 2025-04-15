package com.swiftwheelshubreactive.booking.service.dlq;

import com.swiftwheelshubreactive.booking.mapper.BookingMapper;
import com.swiftwheelshubreactive.booking.mapper.BookingMapperImpl;
import com.swiftwheelshubreactive.booking.producer.bookingprocessing.UpdatedBookingProducerService;
import com.swiftwheelshubreactive.booking.producer.bookingprocessing.UpdatedBookingUpdateCarsProducerService;
import com.swiftwheelshubreactive.booking.util.TestUtil;
import com.swiftwheelshubreactive.dto.BookingResponse;
import com.swiftwheelshubreactive.dto.UpdateCarsRequest;
import com.swiftwheelshubreactive.dto.UpdatedBookingReprocessRequest;
import com.swiftwheelshubreactive.lib.retry.RetryHandler;
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

        when(updatedBookingProducerService.sengBookingResponse(any(BookingResponse.class)))
                .thenReturn(Mono.just(true));
        when(updatedBookingUpdateCarsProducerService.sendUpdateCarsRequest(any(UpdateCarsRequest.class)))
                .thenReturn(Mono.just(true));
        when(retryHandler.retry()).thenReturn(Retry.fixedDelay(0, Duration.ZERO));

        updatedBookingReprocessService.reprocessUpdatedBooking(updatedBookingReprocessRequest)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        verify(bookingMapper).mapToBookingResponse(any(UpdatedBookingReprocessRequest.class));
    }

}
