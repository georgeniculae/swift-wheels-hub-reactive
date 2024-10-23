package com.swiftwheelshubreactive.booking.scheduler;

import com.swiftwheelshubreactive.booking.mapper.BookingMapper;
import com.swiftwheelshubreactive.booking.mapper.BookingMapperImpl;
import com.swiftwheelshubreactive.booking.model.Outbox;
import com.swiftwheelshubreactive.booking.repository.BookingRepository;
import com.swiftwheelshubreactive.booking.service.CarService;
import com.swiftwheelshubreactive.booking.service.OutboxService;
import com.swiftwheelshubreactive.booking.util.TestUtil;
import com.swiftwheelshubreactive.dto.AuthenticationInfo;
import com.swiftwheelshubreactive.dto.CarState;
import com.swiftwheelshubreactive.dto.CarUpdateDetails;
import com.swiftwheelshubreactive.dto.StatusUpdateResponse;
import com.swiftwheelshubreactive.model.Booking;
import com.swiftwheelshubreactive.model.BookingProcessStatus;
import com.swiftwheelshubreactive.model.CarStage;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FailedBookingSchedulerTest {

    @InjectMocks
    private FailedBookingScheduler failedBookingScheduler;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CarService carService;

    @Mock
    private OutboxService outboxService;

    @Spy
    private BookingMapper bookingMapper = new BookingMapperImpl();

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(failedBookingScheduler, "apikey", "apikey");
        ReflectionTestUtils.setField(failedBookingScheduler, "machineRole", "booking_service");
    }

    @Test
    void processFailedBookingsTest_success_failedCreatedBooking() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);
        booking.setBookingProcessStatus(BookingProcessStatus.FAILED_CREATED_BOOKING);

        StatusUpdateResponse statusUpdateResponse =
                TestUtil.getResourceAsJson("/data/SuccessfulStatusUpdateResponse.json", StatusUpdateResponse.class);

        when(bookingRepository.findAllFailedBookings()).thenReturn(Flux.just(booking));
        when(carService.changeCarStatus(any(AuthenticationInfo.class), anyString(), any(CarState.class)))
                .thenReturn(Mono.just(statusUpdateResponse));
        when(outboxService.processBookingSaving(any(Booking.class), any(Outbox.Operation.class))).thenReturn(Mono.just(booking));

        assertDoesNotThrow(() -> failedBookingScheduler.processFailedBookings());

        verify(bookingMapper).getSuccessfulCreatedBooking(any(Booking.class));
    }

    @Test
    void processFailedBookingsTest_success_failedUpdatedBooking() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);
        booking.setPreviousCarId(new ObjectId("64f361caf291ae086e179222"));
        booking.setBookingProcessStatus(BookingProcessStatus.FAILED_UPDATED_BOOKING);

        StatusUpdateResponse statusUpdateResponse =
                TestUtil.getResourceAsJson("/data/SuccessfulStatusUpdateResponse.json", StatusUpdateResponse.class);

        when(bookingRepository.findAllFailedBookings()).thenReturn(Flux.just(booking));
        when(carService.updateCarsStatuses(any(AuthenticationInfo.class), anyList())).thenReturn(Mono.just(statusUpdateResponse));
        when(outboxService.processBookingSaving(any(Booking.class), any(Outbox.Operation.class))).thenReturn(Mono.just(booking));

        assertDoesNotThrow(() -> failedBookingScheduler.processFailedBookings());

        verify(bookingMapper).getSuccessfulUpdatedBooking(any(Booking.class));
    }

    @Test
    void processFailedBookingsTest_success_failedClosedBooking() {
        Booking booking = TestUtil.getResourceAsJson("/data/UpdatedClosedBooking.json", Booking.class);
        booking.setBookingProcessStatus(BookingProcessStatus.FAILED_CLOSED_BOOKING);
        booking.setCarStage(CarStage.AVAILABLE);

        StatusUpdateResponse statusUpdateResponse =
                TestUtil.getResourceAsJson("/data/SuccessfulStatusUpdateResponse.json", StatusUpdateResponse.class);

        when(bookingRepository.findAllFailedBookings()).thenReturn(Flux.just(booking));
        when(carService.updateCarWhenBookingIsFinished(any(AuthenticationInfo.class), any(CarUpdateDetails.class)))
                .thenReturn(Mono.just(statusUpdateResponse));
        when(bookingRepository.save(any(Booking.class))).thenReturn(Mono.just(booking));

        assertDoesNotThrow(() -> failedBookingScheduler.processFailedBookings());

        verify(bookingMapper).getSuccessfulClosedBooking(any(Booking.class));
    }

    @Test
    void processFailedBookingsTest_failedCarServiceCall() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);
        booking.setBookingProcessStatus(BookingProcessStatus.FAILED_CREATED_BOOKING);

        StatusUpdateResponse statusUpdateResponse =
                TestUtil.getResourceAsJson("/data/SuccessfulStatusUpdateResponse.json", StatusUpdateResponse.class);

        when(bookingRepository.findAllFailedBookings()).thenReturn(Flux.just(booking));
        when(carService.changeCarStatus(any(AuthenticationInfo.class), anyString(), any(CarState.class)))
                .thenReturn(Mono.just(statusUpdateResponse));
        when(outboxService.processBookingSaving(any(Booking.class), any(Outbox.Operation.class))).thenReturn(Mono.just(booking));

        assertDoesNotThrow(() -> failedBookingScheduler.processFailedBookings());

        verify(bookingMapper).getSuccessfulCreatedBooking(any(Booking.class));
    }

}
