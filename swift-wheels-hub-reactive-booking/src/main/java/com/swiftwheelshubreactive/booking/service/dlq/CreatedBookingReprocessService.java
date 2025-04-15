package com.swiftwheelshubreactive.booking.service.dlq;

import com.swiftwheelshubreactive.booking.mapper.BookingMapper;
import com.swiftwheelshubreactive.booking.producer.bookingprocessing.CreatedBookingCarUpdateProducerService;
import com.swiftwheelshubreactive.booking.producer.bookingprocessing.CreatedBookingProducerService;
import com.swiftwheelshubreactive.dto.BookingResponse;
import com.swiftwheelshubreactive.dto.CarState;
import com.swiftwheelshubreactive.dto.CarStatusUpdate;
import com.swiftwheelshubreactive.dto.CreatedBookingReprocessRequest;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import com.swiftwheelshubreactive.lib.retry.RetryHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreatedBookingReprocessService {

    private final CreatedBookingProducerService createdBookingProducerService;
    private final CreatedBookingCarUpdateProducerService createdBookingCarUpdateProducerService;
    private final RetryHandler retryHandler;
    private final BookingMapper bookingMapper;

    public Mono<Void> reprocessCreatedBooking(CreatedBookingReprocessRequest createdBookingReprocessRequest) {
        return createdBookingProducerService.sendMessage(getBookingResponse(createdBookingReprocessRequest))
                .filter(Boolean.TRUE::equals)
                .flatMap(_ -> updateCarForNewBooking(createdBookingReprocessRequest.actualCarId()))
                .filter(Boolean.TRUE::equals)
                .retryWhen(retryHandler.retry())
                .switchIfEmpty(Mono.error(new SwiftWheelsHubException("Booking creation reprocess failed")))
                .then()
                .onErrorResume(e -> {
                    log.error("Error while trying to reprocess booking creation: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    private Mono<Boolean> updateCarForNewBooking(String actualCarId) {
        return createdBookingCarUpdateProducerService.sendCarUpdateDetails(getCarStatusUpdate(actualCarId));
    }

    private CarStatusUpdate getCarStatusUpdate(String actualCarId) {
        return CarStatusUpdate.builder()
                .carId(actualCarId)
                .carState(CarState.NOT_AVAILABLE)
                .build();
    }

    private BookingResponse getBookingResponse(CreatedBookingReprocessRequest booking) {
        return bookingMapper.mapToBookingResponse(booking);
    }

}
