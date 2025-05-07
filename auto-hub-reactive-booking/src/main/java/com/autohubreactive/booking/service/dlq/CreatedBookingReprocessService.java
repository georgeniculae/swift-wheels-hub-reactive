package com.autohubreactive.booking.service.dlq;

import com.autohubreactive.booking.mapper.BookingMapper;
import com.autohubreactive.booking.producer.bookingprocessing.CreatedBookingCarUpdateProducerService;
import com.autohubreactive.booking.producer.bookingprocessing.CreatedBookingProducerService;
import com.autohubreactive.dto.common.BookingResponse;
import com.autohubreactive.dto.common.CarState;
import com.autohubreactive.dto.common.CarStatusUpdate;
import com.autohubreactive.dto.booking.CreatedBookingReprocessRequest;
import com.autohubreactive.exception.AutoHubException;
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
    private final BookingMapper bookingMapper;

    public Mono<Void> reprocessCreatedBooking(CreatedBookingReprocessRequest createdBookingReprocessRequest) {
        return updateCarForNewBooking(createdBookingReprocessRequest.actualCarId())
                .then(Mono.defer(() -> createdBookingProducerService.sendCreatedBooking(getBookingResponse(createdBookingReprocessRequest))))
                .onErrorResume(e -> {
                    log.error("Error while trying to reprocess booking creation: {}", e.getMessage());

                    return Mono.error(new AutoHubException(e.getMessage()));
                });
    }

    private Mono<Void> updateCarForNewBooking(String actualCarId) {
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
