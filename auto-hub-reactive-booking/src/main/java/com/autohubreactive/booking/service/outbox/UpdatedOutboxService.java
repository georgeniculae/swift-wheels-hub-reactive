package com.autohubreactive.booking.service.outbox;

import com.autohubreactive.booking.mapper.BookingMapper;
import com.autohubreactive.booking.model.UpdatedOutbox;
import com.autohubreactive.booking.producer.bookingprocessing.UpdatedBookingProducerService;
import com.autohubreactive.booking.producer.bookingprocessing.UpdatedBookingUpdateCarsProducerService;
import com.autohubreactive.booking.producer.dlq.FailedUpdatedBookingDlqProducerService;
import com.autohubreactive.booking.repository.BookingRepository;
import com.autohubreactive.booking.repository.UpdateOutboxRepository;
import com.autohubreactive.dto.common.BookingResponse;
import com.autohubreactive.dto.common.UpdateCarsRequest;
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
                .delayUntil(this::processBooking)
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

    public Mono<Void> processBooking(UpdatedOutbox updatedOutbox) {
        return Mono.just(updatedOutbox)
                .filter(outbox -> !outbox.isCarChanged())
                .switchIfEmpty(Mono.defer(() -> changeCarsStatuses(updatedOutbox)))
                .delayUntil(outbox -> updatedBookingProducerService.sendBookingResponse(getBookingResponse(outbox.getContent())))
                .flatMap(outbox -> unlockCar(outbox.getContent().getActualCarId().toString()))
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
                .thenReturn(updatedOutbox);
    }

    private UpdateCarsRequest getUpdateCarsRequest(String existingCarId, String newCarId) {
        return UpdateCarsRequest.builder()
                .previousCarId(existingCarId)
                .actualCarId(newCarId)
                .build();
    }

    private Mono<Void> reprocessBooking(UpdatedOutbox updatedOutbox) {
        return failedUpdatedBookingDlqProducerService.sendUpdatedBookingReprocessRequest(
                bookingMapper.getUpdatedBookingReprocessRequest(
                        updatedOutbox.getContent(),
                        updatedOutbox.isCarChanged()
                )
        );
    }

    private BookingResponse getBookingResponse(Booking booking) {
        return bookingMapper.mapEntityToDto(booking);
    }

}
