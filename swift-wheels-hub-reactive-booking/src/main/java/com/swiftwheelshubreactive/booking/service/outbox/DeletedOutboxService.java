package com.swiftwheelshubreactive.booking.service.outbox;

import com.swiftwheelshubreactive.booking.mapper.BookingMapper;
import com.swiftwheelshubreactive.booking.model.DeletedOutbox;
import com.swiftwheelshubreactive.booking.producer.bookingprocessing.DeletedBookingProducerService;
import com.swiftwheelshubreactive.booking.producer.dlq.FailedCreatedBookingDlqProducerService;
import com.swiftwheelshubreactive.booking.repository.BookingRepository;
import com.swiftwheelshubreactive.booking.repository.DeletedOutboxRepository;
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
public class DeletedOutboxService extends OutboxService {

    private final DeletedOutboxRepository deletedOutboxRepository;
    private final BookingRepository bookingRepository;
    private final DeletedBookingProducerService deletedBookingProducerService;
    private final FailedCreatedBookingDlqProducerService failedCreatedBookingDlqProducerService;
    private final BookingMapper bookingMapper;

    public DeletedOutboxService(ReactiveRedisOperations<String, String> redisOperations,
                                DeletedOutboxRepository deletedOutboxRepository,
                                BookingRepository bookingRepository,
                                DeletedBookingProducerService deletedBookingProducerService,
                                FailedCreatedBookingDlqProducerService failedCreatedBookingDlqProducerService,
                                BookingMapper bookingMapper) {
        super(redisOperations);
        this.deletedOutboxRepository = deletedOutboxRepository;
        this.bookingRepository = bookingRepository;
        this.deletedBookingProducerService = deletedBookingProducerService;
        this.failedCreatedBookingDlqProducerService = failedCreatedBookingDlqProducerService;
        this.bookingMapper = bookingMapper;
    }

    @Override
    public Flux<Void> handleOutboxes() {
        return deletedOutboxRepository.findAll()
                .concatMap(this::processBooking)
                .concatMap(deletedOutboxRepository::delete)
                .onErrorMap(e -> {
                    log.error("Error while processing/sending booking: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    @Transactional
    public Mono<DeletedOutbox> processBookingDeletion(Booking booking) {
        return bookingRepository.deleteById(booking.getId())
                .then(Mono.defer(() -> processOutboxes(booking)));
    }

    private Mono<DeletedOutbox> processOutboxes(Booking booking) {
        return Mono.just(createOutbox(booking))
                .flatMap(deletedOutboxRepository::save);
    }

    private DeletedOutbox createOutbox(Booking booking) {
        return DeletedOutbox.builder()
                .content(booking)
                .build();
    }

    private Mono<DeletedOutbox> processBooking(DeletedOutbox deletedOutbox) {
        return deletedBookingProducerService.sendMessage(deletedOutbox.getContent().getId().toString())
                .filter(Boolean.TRUE::equals)
                .map(_ -> deletedOutbox);
    }

}
