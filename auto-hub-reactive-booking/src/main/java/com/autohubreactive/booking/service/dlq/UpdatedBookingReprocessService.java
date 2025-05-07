package com.autohubreactive.booking.service.dlq;

import com.autohubreactive.booking.mapper.BookingMapper;
import com.autohubreactive.booking.producer.bookingprocessing.UpdatedBookingProducerService;
import com.autohubreactive.booking.producer.bookingprocessing.UpdatedBookingUpdateCarsProducerService;
import com.autohubreactive.dto.booking.UpdatedBookingReprocessRequest;
import com.autohubreactive.dto.common.BookingResponse;
import com.autohubreactive.dto.common.UpdateCarsRequest;
import com.autohubreactive.exception.AutoHubException;
import com.autohubreactive.lib.retry.RetryHandler;
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
                .flatMap(bookingReprocessRequest -> updatedBookingProducerService.sendBookingResponse(getBookingResponse(bookingReprocessRequest)))
                .retryWhen(retryHandler.retry())
                .onErrorResume(e -> {
                    log.error("Error while trying to reprocess booking update: {}", e.getMessage());

                    return Mono.error(new AutoHubException(e.getMessage()));
                });
    }

    private Mono<UpdatedBookingReprocessRequest> changeCarsStatuses(UpdatedBookingReprocessRequest updatedBookingReprocessRequest) {
        return updatedBookingUpdateCarsProducerService.sendUpdateCarsRequest(
                        getUpdateCarsRequest(
                                updatedBookingReprocessRequest.previousCarId(),
                                updatedBookingReprocessRequest.actualCarId()
                        )
                )
                .thenReturn(updatedBookingReprocessRequest);
    }

    private UpdateCarsRequest getUpdateCarsRequest(String existingCarId, String newCarId) {
        return UpdateCarsRequest.builder()
                .previousCarId(existingCarId)
                .actualCarId(newCarId)
                .build();
    }

    private BookingResponse getBookingResponse(UpdatedBookingReprocessRequest updatedBookingReprocessRequest) {
        return bookingMapper.mapToBookingResponse(updatedBookingReprocessRequest);
    }

}
