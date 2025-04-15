package com.swiftwheelshubreactive.booking.service.dlq;

import com.swiftwheelshubreactive.booking.mapper.BookingMapper;
import com.swiftwheelshubreactive.booking.producer.bookingprocessing.UpdatedBookingProducerService;
import com.swiftwheelshubreactive.booking.producer.bookingprocessing.UpdatedBookingUpdateCarsProducerService;
import com.swiftwheelshubreactive.dto.BookingResponse;
import com.swiftwheelshubreactive.dto.UpdateCarsRequest;
import com.swiftwheelshubreactive.dto.UpdatedBookingReprocessRequest;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import com.swiftwheelshubreactive.lib.retry.RetryHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdatedBookingReprocessService {

    private final UpdatedBookingProducerService updatedBookingProducerService;
    private final UpdatedBookingUpdateCarsProducerService updatedBookingUpdateCarsProducerService;
    private final BookingMapper bookingMapper;
    private final RetryHandler retryHandler;

    public Mono<Void> reprocessUpdatedBooking(UpdatedBookingReprocessRequest updatedBookingReprocessRequest) {
        return Mono.just(updatedBookingReprocessRequest)
                .filter(outbox -> !outbox.isCarChanged())
                .switchIfEmpty(Mono.defer(() -> changeCarsStatuses(updatedBookingReprocessRequest)))
                .flatMap(bookingReprocessRequest -> updatedBookingProducerService.sengBookingResponse(getBookingResponse(bookingReprocessRequest)))
                .filter(Boolean.TRUE::equals)
                .retryWhen(retryHandler.retry())
                .switchIfEmpty(Mono.error(new SwiftWheelsHubException("Booking update reprocess failed")))
                .then()
                .onErrorResume(e -> {
                    log.error("Error while trying to reprocess booking update: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    private Mono<UpdatedBookingReprocessRequest> changeCarsStatuses(UpdatedBookingReprocessRequest updatedBookingReprocessRequest) {
        return updatedBookingUpdateCarsProducerService.sendUpdateCarsRequest(
                        getUpdateCarsRequest(
                                updatedBookingReprocessRequest.previousCarId(),
                                updatedBookingReprocessRequest.actualCarId()
                        )
                )
                .filter(Boolean.TRUE::equals)
                .map(_ -> updatedBookingReprocessRequest);
    }

    private UpdateCarsRequest getUpdateCarsRequest(String existingCarId, String newCarId) {
        return UpdateCarsRequest.builder()
                .previousCarId(existingCarId)
                .actualCarId(newCarId)
                .build();
    }

    private BookingResponse getBookingResponse(UpdatedBookingReprocessRequest booking) {
        return bookingMapper.mapToBookingResponse(booking);
    }

}
