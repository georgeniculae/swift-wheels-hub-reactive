package com.swiftwheelshubreactive.booking.scheduler;

import com.swiftwheelshubreactive.booking.mapper.BookingMapper;
import com.swiftwheelshubreactive.booking.model.Outbox;
import com.swiftwheelshubreactive.booking.repository.BookingRepository;
import com.swiftwheelshubreactive.booking.service.CarService;
import com.swiftwheelshubreactive.booking.service.OutboxService;
import com.swiftwheelshubreactive.dto.AuthenticationInfo;
import com.swiftwheelshubreactive.dto.CarState;
import com.swiftwheelshubreactive.dto.StatusUpdateResponse;
import com.swiftwheelshubreactive.model.Booking;
import com.swiftwheelshubreactive.model.BookingProcessState;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FailedBookingScheduler {

    private final BookingRepository bookingRepository;
    private final CarService carService;
    private final OutboxService outboxService;
    private final BookingMapper bookingMapper;

    @Value("${apikey.secret}")
    private String apikey;

    @Value("${apikey.machine-role}")
    private String machineRole;

    @Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
    public void processFailedBookings() {
        bookingRepository.findAllFailedBookings()
                .flatMap(this::processBookingsByOperation)
                .subscribe();
    }

    private Mono<Booking> processBookingsByOperation(Booking failedBooking) {
        return carService.changeCarStatus(getAuthenticationInfo(), getCarId(failedBooking), CarState.NOT_AVAILABLE)
                .filter(StatusUpdateResponse::isUpdateSuccessful)
                .flatMap(_ -> processFailedBooking(failedBooking));
    }

    private AuthenticationInfo getAuthenticationInfo() {
        return AuthenticationInfo.builder()
                .apikey(apikey)
                .roles(getRoles())
                .build();
    }

    private List<String> getRoles() {
        return List.of(machineRole);
    }

    private String getCarId(Booking booking) {
        return booking.getCarId().toString();
    }

    private Mono<Booking> processFailedBooking(Booking failedBooking) {
        if (BookingProcessState.FAILED_CREATED_BOOKING == failedBooking.getBookingProcessState()) {
            return outboxService.saveBookingAndOutbox(failedBooking, Outbox.Operation.CREATE);
        }

        if (BookingProcessState.FAILED_UPDATED_BOOKING == failedBooking.getBookingProcessState()) {
            return outboxService.saveBookingAndOutbox(failedBooking, Outbox.Operation.UPDATE);
        }

        return bookingRepository.save(processFailedClosedBooking(failedBooking));
    }

    private Booking processFailedClosedBooking(Booking failedBooking) {
        return bookingMapper.createSuccessfulClosedBooking(failedBooking);
    }

}
