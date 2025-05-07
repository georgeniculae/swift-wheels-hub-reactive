package com.autohubreactive.booking.service.dlq;

import com.autohubreactive.booking.mapper.BookingMapper;
import com.autohubreactive.booking.mapper.BookingMapperImpl;
import com.autohubreactive.booking.producer.bookingprocessing.CreatedBookingCarUpdateProducerService;
import com.autohubreactive.booking.producer.bookingprocessing.CreatedBookingProducerService;
import com.autohubreactive.booking.util.TestUtil;
import com.autohubreactive.dto.common.BookingResponse;
import com.autohubreactive.dto.common.CarStatusUpdate;
import com.autohubreactive.dto.booking.CreatedBookingReprocessRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreatedBookingReprocessServiceTest {

    @InjectMocks
    private CreatedBookingReprocessService createdBookingReprocessService;

    @Mock
    private CreatedBookingProducerService createdBookingProducerService;

    @Mock
    private CreatedBookingCarUpdateProducerService createdBookingCarUpdateProducerService;

    @Spy
    private BookingMapper bookingMapper = new BookingMapperImpl();

    @Test
    void reprocessCreatedBookingTest_success() {
        CreatedBookingReprocessRequest createdBookingReprocessRequest =
                TestUtil.getResourceAsJson("/data/CreatedBookingReprocessRequest.json", CreatedBookingReprocessRequest.class);

        when(createdBookingCarUpdateProducerService.sendCarUpdateDetails(any(CarStatusUpdate.class)))
                .thenReturn(Mono.empty());
        when(createdBookingProducerService.sendCreatedBooking(any(BookingResponse.class))).thenReturn(Mono.empty());

        createdBookingReprocessService.reprocessCreatedBooking(createdBookingReprocessRequest)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        verify(bookingMapper).mapToBookingResponse(any(CreatedBookingReprocessRequest.class));
    }

    @Test
    void reprocessCreatedBookingTest_errorOnBookingSending() {
        CreatedBookingReprocessRequest createdBookingReprocessRequest =
                TestUtil.getResourceAsJson("/data/CreatedBookingReprocessRequest.json", CreatedBookingReprocessRequest.class);

        when(createdBookingCarUpdateProducerService.sendCarUpdateDetails(any(CarStatusUpdate.class)))
                .thenReturn(Mono.error(new RuntimeException("Test")));

        createdBookingReprocessService.reprocessCreatedBooking(createdBookingReprocessRequest)
                .as(StepVerifier::create)
                .expectError()
                .verify();

        verify(createdBookingProducerService, never()).sendCreatedBooking(any(BookingResponse.class));
    }

}
