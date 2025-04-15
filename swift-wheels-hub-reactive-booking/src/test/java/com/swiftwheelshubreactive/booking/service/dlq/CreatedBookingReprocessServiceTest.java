package com.swiftwheelshubreactive.booking.service.dlq;

import com.swiftwheelshubreactive.booking.mapper.BookingMapper;
import com.swiftwheelshubreactive.booking.mapper.BookingMapperImpl;
import com.swiftwheelshubreactive.booking.producer.bookingprocessing.CreatedBookingCarUpdateProducerService;
import com.swiftwheelshubreactive.booking.producer.bookingprocessing.CreatedBookingProducerService;
import com.swiftwheelshubreactive.booking.util.TestUtil;
import com.swiftwheelshubreactive.dto.BookingResponse;
import com.swiftwheelshubreactive.dto.CarStatusUpdate;
import com.swiftwheelshubreactive.dto.CreatedBookingReprocessRequest;
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

    @Mock
    private RetryHandler retryHandler;

    @Spy
    private BookingMapper bookingMapper = new BookingMapperImpl();

    @Test
    void reprocessCreatedBookingTest_success() {
        CreatedBookingReprocessRequest createdBookingReprocessRequest =
                TestUtil.getResourceAsJson("/data/CreatedBookingReprocessRequest.json", CreatedBookingReprocessRequest.class);

        when(createdBookingProducerService.sendMessage(any(BookingResponse.class))).thenReturn(Mono.just(true));
        when(createdBookingCarUpdateProducerService.sendCarUpdateDetails(any(CarStatusUpdate.class)))
                .thenReturn(Mono.just(true));
        when(retryHandler.retry()).thenReturn(Retry.fixedDelay(0, Duration.ZERO));

        createdBookingReprocessService.reprocessCreatedBooking(createdBookingReprocessRequest)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        verify(bookingMapper).mapToBookingResponse(any(CreatedBookingReprocessRequest.class));
    }

    @Test
    void reprocessCreatedBookingTest_bookingSendFailed() {
        CreatedBookingReprocessRequest createdBookingReprocessRequest =
                TestUtil.getResourceAsJson("/data/CreatedBookingReprocessRequest.json", CreatedBookingReprocessRequest.class);

        when(createdBookingProducerService.sendMessage(any(BookingResponse.class))).thenReturn(Mono.just(false));
        when(retryHandler.retry()).thenReturn(Retry.fixedDelay(0, Duration.ZERO));

        createdBookingReprocessService.reprocessCreatedBooking(createdBookingReprocessRequest)
                .as(StepVerifier::create)
                .expectError()
                .verify();

        verify(createdBookingCarUpdateProducerService, never()).sendCarUpdateDetails(any(CarStatusUpdate.class));
    }

    @Test
    void reprocessCreatedBookingTest_carUpdateSendFailed() {
        CreatedBookingReprocessRequest createdBookingReprocessRequest =
                TestUtil.getResourceAsJson("/data/CreatedBookingReprocessRequest.json", CreatedBookingReprocessRequest.class);

        when(createdBookingProducerService.sendMessage(any(BookingResponse.class))).thenReturn(Mono.just(true));
        when(createdBookingCarUpdateProducerService.sendCarUpdateDetails(any(CarStatusUpdate.class)))
                .thenReturn(Mono.just(false));
        when(retryHandler.retry()).thenReturn(Retry.fixedDelay(0, Duration.ZERO));

        createdBookingReprocessService.reprocessCreatedBooking(createdBookingReprocessRequest)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void reprocessCreatedBookingTest_errorOnBookingSending() {
        CreatedBookingReprocessRequest createdBookingReprocessRequest =
                TestUtil.getResourceAsJson("/data/CreatedBookingReprocessRequest.json", CreatedBookingReprocessRequest.class);

        when(createdBookingProducerService.sendMessage(any(BookingResponse.class)))
                .thenReturn(Mono.error(new RuntimeException("Test")));
        when(retryHandler.retry()).thenReturn(Retry.fixedDelay(0, Duration.ZERO));

        createdBookingReprocessService.reprocessCreatedBooking(createdBookingReprocessRequest)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

}
