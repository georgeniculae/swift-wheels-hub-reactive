package com.swiftwheelshubreactive.booking.service.outbox;

import com.swiftwheelshubreactive.booking.mapper.BookingMapper;
import com.swiftwheelshubreactive.booking.model.UpdatedOutbox;
import com.swiftwheelshubreactive.booking.producer.bookingprocessing.UpdatedBookingProducerService;
import com.swiftwheelshubreactive.booking.producer.bookingprocessing.UpdatedBookingUpdateCarsProducerService;
import com.swiftwheelshubreactive.booking.producer.dlq.FailedUpdatedBookingDlqProducerService;
import com.swiftwheelshubreactive.booking.repository.BookingRepository;
import com.swiftwheelshubreactive.booking.repository.UpdateOutboxRepository;
import com.swiftwheelshubreactive.dto.BookingResponse;
import com.swiftwheelshubreactive.dto.UpdateCarsRequest;
import com.swiftwheelshubreactive.lib.exceptionhandling.ExceptionUtil;
import com.swiftwheelshubreactive.model.Booking;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class UpdatedOutboxService extends OutboxService {

    private final UpdateOutboxRepository updateOutboxRepository;
    private final BookingRepository bookingRepository;
    private final UpdatedBookingProducerService updatedBookingProducerService;
    private final UpdatedBookingUpdateCarsProducerService updatedBookingUpdateCarsProducerService;
    private final FailedUpdatedBookingDlqProducerService failedUpdatedBookingDlqProducerService;
    private final BookingMapper bookingMapper;

    public UpdatedOutboxService(ReactiveRedisOperations<String, String> redisOperations,
                                UpdateOutboxRepository updateOutboxRepository,
                                BookingRepository bookingRepository,
                                UpdatedBookingProducerService updatedBookingProducerService,
                                FailedUpdatedBookingDlqProducerService failedUpdatedBookingDlqProducerService,
                                UpdatedBookingUpdateCarsProducerService updatedBookingUpdateCarsProducerService,
                                BookingMapper bookingMapper) {
        super(redisOperations);
        this.updateOutboxRepository = updateOutboxRepository;
        this.bookingRepository = bookingRepository;
        this.updatedBookingProducerService = updatedBookingProducerService;
        this.failedUpdatedBookingDlqProducerService = failedUpdatedBookingDlqProducerService;
        this.updatedBookingUpdateCarsProducerService = updatedBookingUpdateCarsProducerService;
        this.bookingMapper = bookingMapper;
    }

    @Override
    public Flux<Void> handleOutboxes() {
        return updateOutboxRepository.findAll()
                .concatMap(this::processBooking)
                .concatMap(updateOutboxRepository::delete)
                .onErrorMap(e -> {
                    log.error("Error while processing/sending booking: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    @Transactional
    public Mono<Booking> processBookingUpdate(Booking booking) {
        return bookingRepository.save(booking)
                .flatMap(this::processOutboxes);
    }

    private Mono<Booking> processOutboxes(Booking booking) {
        return Mono.just(createOutbox(booking))
                .flatMap(updateOutboxRepository::save)
                .map(UpdatedOutbox::getContent);
    }

    private UpdatedOutbox createOutbox(Booking booking) {
        return UpdatedOutbox.builder()
                .content(booking)
                .build();
    }

    public Mono<UpdatedOutbox> processBooking(UpdatedOutbox updatedOutbox) {
        return Mono.just(updatedOutbox)
                .filter(outbox -> !outbox.isCarChanged())
                .switchIfEmpty(Mono.defer(() -> changeCarsStatuses(updatedOutbox)))
                .flatMap(savedOutbox -> updatedBookingProducerService.sengBookingResponse(getBookingResponse(savedOutbox.getContent())))
                .filter(Boolean.TRUE::equals)
                .map(_ -> updatedOutbox)
                .delayUntil(outbox -> unlockCar(outbox.getContent().getActualCarId().toString()))
                .switchIfEmpty(Mono.defer(() -> reprocessBooking(updatedOutbox)))
                .onErrorResume(e -> {
                    log.error("Error while processing booking: {}", e.getMessage());

                    return reprocessBooking(updatedOutbox);
                });
    }

    private Mono<UpdatedOutbox> changeCarsStatuses(UpdatedOutbox updatedOutbox) {
        return updatedBookingUpdateCarsProducerService.sendUpdateCarsRequest(
                        getUpdateCarsRequest(
                                updatedOutbox.getContent().getPreviousCarId().toString(),
                                updatedOutbox.getContent().getActualCarId().toString()
                        )
                )
                .filter(Boolean.TRUE::equals)
                .map(_ -> updatedOutbox);
    }

    private UpdateCarsRequest getUpdateCarsRequest(String existingCarId, String newCarId) {
        return UpdateCarsRequest.builder()
                .previousCarId(existingCarId)
                .actualCarId(newCarId)
                .build();
    }

    private Mono<UpdatedOutbox> reprocessBooking(UpdatedOutbox updatedOutbox) {
        return failedUpdatedBookingDlqProducerService.reprocessUpdatedBooking(
                        bookingMapper.getUpdatedBookingReprocessRequest(
                                updatedOutbox.getContent(),
                                updatedOutbox.isCarChanged()
                        )
                )
                .then(Mono.empty());
    }

    private BookingResponse getBookingResponse(Booking booking) {
        return bookingMapper.mapEntityToDto(booking);
    }

}
