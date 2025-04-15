package com.swiftwheelshubreactive.booking.service.outbox;

import com.swiftwheelshubreactive.booking.model.DeletedOutbox;
import com.swiftwheelshubreactive.booking.producer.bookingprocessing.DeletedBookingProducerService;
import com.swiftwheelshubreactive.booking.repository.BookingRepository;
import com.swiftwheelshubreactive.booking.repository.DeletedOutboxRepository;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
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

    public DeletedOutboxService(ReactiveRedisOperations<String, String> redisOperations,
                                DeletedOutboxRepository deletedOutboxRepository,
                                BookingRepository bookingRepository,
                                DeletedBookingProducerService deletedBookingProducerService) {
        super(redisOperations);
        this.deletedOutboxRepository = deletedOutboxRepository;
        this.bookingRepository = bookingRepository;
        this.deletedBookingProducerService = deletedBookingProducerService;
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
                .map(_ -> deletedOutbox)
                .switchIfEmpty(Mono.error(new SwiftWheelsHubException("Failed to send booking id for deletion")));
    }

}
