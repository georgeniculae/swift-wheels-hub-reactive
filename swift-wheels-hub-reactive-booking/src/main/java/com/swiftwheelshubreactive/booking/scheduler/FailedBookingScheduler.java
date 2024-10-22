package com.swiftwheelshubreactive.booking.scheduler;

import com.swiftwheelshubreactive.booking.mapper.BookingMapper;
import com.swiftwheelshubreactive.booking.model.Outbox;
import com.swiftwheelshubreactive.booking.repository.BookingRepository;
import com.swiftwheelshubreactive.booking.service.CarService;
import com.swiftwheelshubreactive.booking.service.OutboxService;
import com.swiftwheelshubreactive.dto.AuthenticationInfo;
import com.swiftwheelshubreactive.dto.CarState;
import com.swiftwheelshubreactive.dto.CarUpdateDetails;
import com.swiftwheelshubreactive.dto.StatusUpdateResponse;
import com.swiftwheelshubreactive.dto.UpdateCarRequest;
import com.swiftwheelshubreactive.model.Booking;
import com.swiftwheelshubreactive.model.BookingProcessStatus;
import com.swiftwheelshubreactive.model.CarStage;
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
        return processCarServiceCall(failedBooking)
                .filter(StatusUpdateResponse::isUpdateSuccessful)
                .flatMap(_ -> processFailedBooking(failedBooking));
    }

    private Mono<StatusUpdateResponse> processCarServiceCall(Booking booking) {
        if (BookingProcessStatus.FAILED_CREATED_BOOKING == booking.getBookingProcessStatus()) {
            return carService.changeCarStatus(
                    getAuthenticationInfo(),
                    booking.getActualCarId().toString(),
                    CarState.NOT_AVAILABLE
            );
        }

        if (BookingProcessStatus.FAILED_UPDATED_BOOKING == booking.getBookingProcessStatus()) {
            return carService.updateCarsStatuses(
                    getAuthenticationInfo(),
                    getCarsToUpdate(booking.getPreviousCarId().toString(), booking.getActualCarId().toString())
            );
        }

        return carService.updateCarWhenBookingIsFinished(getAuthenticationInfo(), getCarUpdateDetails(booking));
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

    private List<UpdateCarRequest> getCarsToUpdate(String previousCarId, String newCarId) {
        return List.of(
                new UpdateCarRequest(previousCarId, CarState.AVAILABLE),
                new UpdateCarRequest(newCarId, CarState.NOT_AVAILABLE)
        );
    }

    private CarUpdateDetails getCarUpdateDetails(Booking booking) {
        return CarUpdateDetails.builder()
                .carId(booking.getActualCarId().toString())
                .receptionistEmployeeId(booking.getReturnBranchId().toString())
                .carState(getCarPhase(booking.getCarStage()))
                .build();
    }

    private CarState getCarPhase(CarStage carStage) {
        return switch (carStage) {
            case AVAILABLE -> CarState.AVAILABLE;
            case BROKEN -> CarState.BROKEN;
        };
    }

    private Mono<Booking> processFailedBooking(Booking failedBooking) {
        if (BookingProcessStatus.FAILED_CREATED_BOOKING == failedBooking.getBookingProcessStatus()) {
            return outboxService.processBookingSaving(bookingMapper.getSuccessfulCreatedBooking(failedBooking), Outbox.Operation.CREATE);
        }

        if (BookingProcessStatus.FAILED_UPDATED_BOOKING == failedBooking.getBookingProcessStatus()) {
            return outboxService.processBookingSaving(bookingMapper.getSuccessfulUpdatedBooking(failedBooking), Outbox.Operation.UPDATE);
        }

        return bookingRepository.save(processFailedClosedBooking(failedBooking));
    }

    private Booking processFailedClosedBooking(Booking failedBooking) {
        return bookingMapper.getSuccessfulClosedBooking(failedBooking);
    }

}
