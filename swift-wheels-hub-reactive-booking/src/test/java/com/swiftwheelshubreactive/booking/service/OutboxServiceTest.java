package com.swiftwheelshubreactive.booking.service;

import com.swiftwheelshubreactive.booking.mapper.BookingMapper;
import com.swiftwheelshubreactive.booking.mapper.BookingMapperImpl;
import com.swiftwheelshubreactive.booking.model.Outbox;
import com.swiftwheelshubreactive.booking.producer.CreatedBookingProducerService;
import com.swiftwheelshubreactive.booking.producer.DeletedBookingProducerService;
import com.swiftwheelshubreactive.booking.producer.UpdatedBookingProducerService;
import com.swiftwheelshubreactive.booking.repository.BookingRepository;
import com.swiftwheelshubreactive.booking.repository.OutboxRepository;
import com.swiftwheelshubreactive.booking.util.TestUtil;
import com.swiftwheelshubreactive.dto.BookingResponse;
import com.swiftwheelshubreactive.model.Booking;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxServiceTest {

    @InjectMocks
    private OutboxService outboxService;

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CreatedBookingProducerService createdBookingProducerService;

    @Mock
    private UpdatedBookingProducerService updatedBookingProducerService;

    @Mock
    private DeletedBookingProducerService deletedBookingProducerService;

    @Spy
    private BookingMapper bookingMapper = new BookingMapperImpl();

    @Test
    void handleOutboxesTest_success_createOperation() {
        Outbox outbox = TestUtil.getResourceAsJson("/data/Outbox.json", Outbox.class);

        when(outboxRepository.findAll()).thenReturn(Flux.just(outbox));
        when(createdBookingProducerService.sendMessage(any(BookingResponse.class))).thenReturn(Mono.just(true));
        when(outboxRepository.delete(any(Outbox.class))).thenReturn(Mono.empty());

        StepVerifier.create(outboxService.handleOutboxes())
                .expectComplete()
                .verify();

        verify(bookingMapper).mapEntityToDto(any(Booking.class));
    }

    @Test
    void handleOutboxesTest_errorOnFindingAll() {
        when(outboxRepository.findAll()).thenReturn(Flux.error(new Throwable()));

        StepVerifier.create(outboxService.handleOutboxes())
                .expectError()
                .verify();
    }

    @Test
    void handleOutboxesTest_success_updateOperation() {
        Outbox outbox = TestUtil.getResourceAsJson("/data/Outbox.json", Outbox.class);
        outbox.setOperation(Outbox.Operation.UPDATE);

        when(outboxRepository.findAll()).thenReturn(Flux.just(outbox));
        when(updatedBookingProducerService.sendMessage(any(BookingResponse.class))).thenReturn(Mono.just(true));
        when(outboxRepository.delete(any(Outbox.class))).thenReturn(Mono.empty());

        StepVerifier.create(outboxService.handleOutboxes())
                .expectComplete()
                .verify();
    }

    @Test
    void handleOutboxesTest_success_deleteOperation() {
        Outbox outbox = TestUtil.getResourceAsJson("/data/Outbox.json", Outbox.class);
        outbox.setOperation(Outbox.Operation.DELETE);

        when(outboxRepository.findAll()).thenReturn(Flux.just(outbox));
        when(deletedBookingProducerService.sendMessage(anyString())).thenReturn(Mono.just(true));
        when(outboxRepository.delete(any(Outbox.class))).thenReturn(Mono.empty());

        StepVerifier.create(outboxService.handleOutboxes())
                .expectComplete()
                .verify();
    }

    @Test
    void handleOutboxesTest_errorOnSendingSavedBooking() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);
        Outbox outbox = TestUtil.getResourceAsJson("/data/Outbox.json", Outbox.class);

        when(bookingRepository.save(any(Booking.class))).thenReturn(Mono.just(booking));
        when(outboxRepository.save(any(Outbox.class))).thenReturn(Mono.just(outbox));

        StepVerifier.create(outboxService.processBookingSaving(booking, Outbox.Operation.CREATE))
                .expectNext(outbox.getContent())
                .verifyComplete();
    }

    @Test
    void handleOutboxesTest_errorOnSendingUpdatedBooking() {
        Outbox outbox = TestUtil.getResourceAsJson("/data/Outbox.json", Outbox.class);
        outbox.setOperation(Outbox.Operation.UPDATE);

        when(outboxRepository.findAll()).thenReturn(Flux.just(outbox));
        when(updatedBookingProducerService.sendMessage(any(BookingResponse.class))).thenReturn(Mono.just(false));

        StepVerifier.create(outboxService.handleOutboxes())
                .expectError()
                .verify();
    }

    @Test
    void processBookingSavingTransactionalTest_successUpdateOperation() {
        Outbox outbox = TestUtil.getResourceAsJson("/data/Outbox.json", Outbox.class);
        outbox.setOperation(Outbox.Operation.UPDATE);

        when(outboxRepository.findAll()).thenReturn(Flux.just(outbox));
        when(updatedBookingProducerService.sendMessage(any(BookingResponse.class))).thenReturn(Mono.just(true));
        when(outboxRepository.delete(any(Outbox.class))).thenReturn(Mono.empty());

        StepVerifier.create(outboxService.handleOutboxes())
                .expectComplete()
                .verify();
    }

    @Test
    void processBookingDeletionTest_success() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);
        Outbox outbox = TestUtil.getResourceAsJson("/data/Outbox.json", Outbox.class);

        when(bookingRepository.deleteById(any(ObjectId.class))).thenReturn(Mono.empty());
        when(outboxRepository.save(any(Outbox.class))).thenReturn(Mono.just(outbox));

        StepVerifier.create(outboxService.processBookingDeletion(booking, Outbox.Operation.DELETE))
                .expectNext(outbox)
                .verifyComplete();
    }

}
