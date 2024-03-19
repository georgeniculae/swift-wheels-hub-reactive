package com.swiftwheelshubreactive.booking.service;

import com.swiftwheelshubreactive.booking.mapper.BookingMapper;
import com.swiftwheelshubreactive.booking.model.Outbox;
import com.swiftwheelshubreactive.booking.repository.BookingRepository;
import com.swiftwheelshubreactive.booking.repository.OutboxRepository;
import com.swiftwheelshubreactive.dto.BookingResponse;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
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
    private final SavedBookingProducerService savedBookingProducerService;
    private final UpdatedBookingProducerService updatedBookingProducerService;
    private final DeletedBookingProducerService deletedBookingProducerService;
    private final BookingMapper bookingMapper;

    public Flux<Void> handleOutboxes() {
        return outboxRepository.findAll()
                .concatMap(this::processBooking)
                .concatMap(outboxRepository::delete)
                .onErrorMap(e -> {
                    log.error("Error while processing/sending booking: {}", e.getMessage());

                    return new SwiftWheelsHubException(e.getMessage());
                });
    }

    @Transactional
    public Mono<Outbox> saveBookingAndOutboxTransactional(Booking booking, Outbox.Operation operation) {
        return bookingRepository.save(booking)
                .flatMap(savedBooking -> saveOutbox(savedBooking, operation));
    }

    @Transactional
    public Mono<Booking> processBookingDeletion(Booking booking, Outbox.Operation operation) {
        return bookingRepository.deleteByCustomerUsername(booking.getCustomerUsername())
                .then(saveOutbox(booking, operation))
                .map(Outbox::getContent);
    }

    private Mono<Outbox> saveOutbox(Booking savedBooking, Outbox.Operation operation) {
        Outbox outbox = Outbox.builder()
                .operation(operation)
                .content(savedBooking)
                .build();

        return outboxRepository.save(outbox);
    }

    private Mono<Outbox> processBooking(Outbox outbox) {
        return sendBookingToCorrespondingTopic(outbox)
                .filter(Boolean.TRUE::equals)
                .switchIfEmpty(Mono.error(new SwiftWheelsHubException("Sending booking failed")))
                .map(response -> outbox);
    }

    private Mono<Boolean> sendBookingToCorrespondingTopic(Outbox outbox) {
        if (Outbox.Operation.CREATE.equals(outbox.getOperation())) {
            return savedBookingProducerService.sendMessage(getBookingResponse(outbox.getContent()));
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
