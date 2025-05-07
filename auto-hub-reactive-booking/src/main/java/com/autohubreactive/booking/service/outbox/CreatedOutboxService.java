package com.autohubreactive.booking.service.outbox;

import com.autohubreactive.booking.mapper.BookingMapper;
import com.autohubreactive.booking.model.CreatedOutbox;
import com.autohubreactive.booking.producer.bookingprocessing.CreatedBookingCarUpdateProducerService;
import com.autohubreactive.booking.producer.bookingprocessing.CreatedBookingProducerService;
import com.autohubreactive.booking.producer.dlq.FailedCreatedBookingDlqProducerService;
import com.autohubreactive.booking.repository.BookingRepository;
import com.autohubreactive.booking.repository.CreatedOutboxRepository;
import com.autohubreactive.dto.common.BookingResponse;
import com.autohubreactive.dto.common.CarState;
import com.autohubreactive.dto.common.CarStatusUpdate;
import com.autohubreactive.lib.exceptionhandling.ExceptionUtil;
import com.autohubreactive.model.booking.Booking;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CreatedOutboxService extends OutboxService {

    private final CreatedOutboxRepository createdOutboxRepository;
    private final BookingRepository bookingRepository;
    private final CreatedBookingProducerService createdBookingProducerService;
    private final CreatedBookingCarUpdateProducerService createdBookingCarUpdateProducerService;
    private final FailedCreatedBookingDlqProducerService failedCreatedBookingDlqProducerService;
    private final BookingMapper bookingMapper;

    public CreatedOutboxService(ReactiveRedisOperations<String, String> redisOperations,
                                CreatedOutboxRepository createdOutboxRepository,
                                BookingRepository bookingRepository,
                                CreatedBookingProducerService createdBookingProducerService,
                                CreatedBookingCarUpdateProducerService createdBookingCarUpdateProducerService,
                                FailedCreatedBookingDlqProducerService failedCreatedBookingDlqProducerService,
                                BookingMapper bookingMapper) {
        super(redisOperations);
        this.createdOutboxRepository = createdOutboxRepository;
        this.bookingRepository = bookingRepository;
        this.createdBookingProducerService = createdBookingProducerService;
        this.createdBookingCarUpdateProducerService = createdBookingCarUpdateProducerService;
        this.failedCreatedBookingDlqProducerService = failedCreatedBookingDlqProducerService;
        this.bookingMapper = bookingMapper;
    }

    @Override
    public Flux<Void> handleOutboxes() {
        return createdOutboxRepository.findAll()
                .delayUntil(this::processBooking)
                .concatMap(createdOutboxRepository::delete)
                .onErrorMap(e -> {
                    log.error("Error while creating booking: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    @Transactional
    public Mono<Booking> processBookingSave(Booking booking) {
        return bookingRepository.save(booking)
                .flatMap(this::saveOutbox)
                .map(CreatedOutbox::getContent);
    }

    private Mono<CreatedOutbox> saveOutbox(Booking savedBooking) {
        return createdOutboxRepository.save(createOutbox(savedBooking));
    }

    private CreatedOutbox createOutbox(Booking booking) {
        return CreatedOutbox.builder()
                .content(booking)
                .build();
    }

    private Mono<Void> processBooking(CreatedOutbox createdOutbox) {
        return updateCarForNewBooking(createdOutbox.getContent())
                .then(Mono.defer(() -> unlockCar(createdOutbox.getContent().getActualCarId().toString())))
                .then(Mono.defer(() -> createdBookingProducerService.sendCreatedBooking(getBookingResponse(createdOutbox.getContent()))))
                .onErrorResume(e -> {
                    log.error("Error while processing booking: {}", e.getMessage());

                    return reprocessBooking(createdOutbox);
                });
    }

    private Mono<Void> reprocessBooking(CreatedOutbox createdOutbox) {
        return failedCreatedBookingDlqProducerService.sendCreatedBookingReprocessRequest(bookingMapper.getCreatedBookingReprocessRequest(createdOutbox.getContent()));
    }

    private Mono<Void> updateCarForNewBooking(Booking booking) {
        return createdBookingCarUpdateProducerService.sendCarUpdateDetails(getCarStatusUpdate(booking.getActualCarId().toString()));
    }

    private CarStatusUpdate getCarStatusUpdate(String actualCarId) {
        return CarStatusUpdate.builder()
                .carId(actualCarId)
                .carState(CarState.NOT_AVAILABLE)
                .build();
    }

    private BookingResponse getBookingResponse(Booking booking) {
        return bookingMapper.mapEntityToDto(booking);
    }

}
