package com.carrental.booking.service;

import com.carrental.booking.mapper.BookingMapper;
import com.carrental.booking.model.Outbox;
import com.carrental.booking.repository.BookingRepository;
import com.carrental.booking.repository.OutboxRepository;
import com.swiftwheelshub.lib.exceptionhandling.SwiftWheelsHubException;
import com.swiftwheelshub.model.Booking;
import com.carrental.dto.BookingDto;
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
                .onErrorResume(e -> {
                    log.error("Error while processing/sending booking: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    @Transactional
    public Mono<Outbox> saveBookingAndOutboxTransactional(Booking booking, Outbox.Operation operation) {
        return bookingRepository.save(booking)
                .flatMap(savedBooking -> saveOutbox(savedBooking, operation));
    }

    @Transactional
    public Mono<Booking> processBookingDeletion(Booking booking, Outbox.Operation operation) {
        return bookingRepository.deleteById(booking.getCarId())
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
        return Mono.just(outbox)
                .flatMap(createdOutbox -> sendBookingToCorrespondingTopic(outbox, createdOutbox))
                .filter(Boolean.TRUE::equals)
                .switchIfEmpty(Mono.error(new SwiftWheelsHubException("Sending booking failed")))
                .map(response -> outbox);
    }

    private Mono<Boolean> sendBookingToCorrespondingTopic(Outbox outbox, Outbox createdOutbox) {
        if (Outbox.Operation.CREATE.equals(createdOutbox.getOperation())) {
            return savedBookingProducerService.sendMessage(getBookingDto(outbox.getContent()));
        }

        if (Outbox.Operation.UPDATE.equals(createdOutbox.getOperation())) {
            return updatedBookingProducerService.sendMessage(getBookingDto(outbox.getContent()));
        }

        return deletedBookingProducerService.sendMessage(outbox.getContent().getId().toString());
    }

    private BookingDto getBookingDto(Booking booking) {
        return bookingMapper.mapEntityToDto(booking);
    }

}
