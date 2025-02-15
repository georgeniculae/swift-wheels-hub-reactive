package com.swiftwheelshubreactive.booking.service;

import com.swiftwheelshubreactive.booking.mapper.BookingMapper;
import com.swiftwheelshubreactive.booking.model.Outbox;
import com.swiftwheelshubreactive.booking.producer.CreatedBookingProducerService;
import com.swiftwheelshubreactive.booking.producer.DeletedBookingProducerService;
import com.swiftwheelshubreactive.booking.producer.UpdatedBookingProducerService;
import com.swiftwheelshubreactive.booking.repository.BookingRepository;
import com.swiftwheelshubreactive.booking.repository.OutboxRepository;
import com.swiftwheelshubreactive.dto.BookingResponse;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import com.swiftwheelshubreactive.lib.exceptionhandling.ExceptionUtil;
import com.swiftwheelshubreactive.model.Booking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final BookingRepository bookingRepository;
    private final CreatedBookingProducerService createdBookingProducerService;
    private final UpdatedBookingProducerService updatedBookingProducerService;
    private final DeletedBookingProducerService deletedBookingProducerService;
    private final BookingMapper bookingMapper;

    public Flux<Void> handleOutboxes() {
        return outboxRepository.findAll()
                .concatMap(this::processBooking)
                .concatMap(outboxRepository::delete)
                .onErrorMap(e -> {
                    log.error("Error while processing/sending booking: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    @Transactional
    public Mono<Booking> processBookingSaving(Booking booking, Outbox.Operation operation) {
        return bookingRepository.save(booking)
                .flatMap(savedBooking -> saveOutbox(savedBooking, operation));
    }

    @Transactional
    public Mono<Outbox> processBookingDeletion(Booking booking, Outbox.Operation operation) {
        return bookingRepository.deleteById(booking.getId())
                .then(Mono.defer(() -> processOutboxes(booking, operation)));
    }

    private Mono<Booking> saveOutbox(Booking savedBooking, Outbox.Operation operation) {
        return outboxRepository.save(createOutbox(savedBooking, operation))
                .map(Outbox::getContent);
    }

    private Mono<Outbox> processOutboxes(Booking booking, Outbox.Operation operation) {
        return Mono.just(createOutbox(booking, operation))
                .flatMap(outboxRepository::save);
    }

    private Outbox createOutbox(Booking booking, Outbox.Operation operation) {
        return Outbox.builder()
                .operation(operation)
                .content(booking)
                .build();
    }

    private Mono<Outbox> processBooking(Outbox outbox) {
        return sendBookingToCorrespondingTopic(outbox)
                .filter(Boolean.TRUE::equals)
                .switchIfEmpty(Mono.error(new SwiftWheelsHubException("Sending booking failed")))
                .map(_ -> outbox);
    }

    private Mono<Boolean> sendBookingToCorrespondingTopic(Outbox outbox) {
        if (Outbox.Operation.CREATE.equals(outbox.getOperation())) {
            return createdBookingProducerService.sendMessage(getBookingResponse(outbox.getContent()));
        }

        if (Outbox.Operation.UPDATE.equals(outbox.getOperation())) {
            return updatedBookingProducerService.sendMessage(getBookingResponse(outbox.getContent()));
        }

        return deletedBookingProducerService.sendMessage(outbox.getContent().getId().toString());
    }

    private BookingResponse getBookingResponse(Booking booking) {
        return bookingMapper.mapEntityToDto(booking);
    }

}
