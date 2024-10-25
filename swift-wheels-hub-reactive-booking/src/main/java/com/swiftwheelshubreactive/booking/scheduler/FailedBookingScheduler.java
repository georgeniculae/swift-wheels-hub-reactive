package com.swiftwheelshubreactive.booking.scheduler;

import com.swiftwheelshubreactive.booking.mapper.BookingMapper;
import com.swiftwheelshubreactive.booking.model.Outbox;
import com.swiftwheelshubreactive.booking.repository.BookingRepository;
import com.swiftwheelshubreactive.booking.service.CarService;
import com.swiftwheelshubreactive.booking.service.OutboxService;
import com.swiftwheelshubreactive.dto.AuthenticationInfo;
import com.swiftwheelshubreactive.dto.CarState;
import com.swiftwheelshubreactive.dto.StatusUpdateResponse;
import com.swiftwheelshubreactive.dto.UpdateCarRequest;
import com.swiftwheelshubreactive.model.Booking;
import com.swiftwheelshubreactive.model.BookingProcessStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
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
                .onBackpressureBuffer()
                .flatMap(this::processBookingsByOperation)
                .subscribe();
    }

    private Mono<Booking> processBookingsByOperation(Booking failedBooking) {
        return processCarServiceCall(failedBooking)
                .filter(StatusUpdateResponse::isUpdateSuccessful)
                .flatMap(_ -> processFailedBooking(failedBooking));
    }

    private Mono<StatusUpdateResponse> processCarServiceCall(Booking failedBooking) {
        return Mono.just(failedBooking)
                .filter(booking -> BookingProcessStatus.FAILED_CREATED_BOOKING == booking.getBookingProcessStatus())
                .flatMap(booking -> carService.changeCarStatus(
                                getAuthenticationInfo(),
                                booking.getActualCarId().toString(),
                                CarState.NOT_AVAILABLE,
                                0
                        )
                )
                .switchIfEmpty(
                        Mono.defer(() ->
                                carService.updateCarsStatuses(
                                        getAuthenticationInfo(),
                                        getCarsToUpdate(failedBooking.getPreviousCarId().toString(), failedBooking.getActualCarId().toString()),
                                        0
                                )
                        )
                );
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

    private Mono<Booking> processFailedBooking(Booking failedBooking) {
        return Mono.just(failedBooking)
                .filter(booking -> BookingProcessStatus.FAILED_CREATED_BOOKING == booking.getBookingProcessStatus())
                .flatMap(booking -> outboxService.processBookingSaving(bookingMapper.getSuccessfulCreatedBooking(booking), Outbox.Operation.CREATE))
                .switchIfEmpty(Mono.defer(() -> outboxService.processBookingSaving(bookingMapper.getSuccessfulUpdatedBooking(failedBooking), Outbox.Operation.UPDATE)));
    }

}
