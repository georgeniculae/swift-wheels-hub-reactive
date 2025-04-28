package com.autohubreactive.booking.service.outbox;

import com.autohubreactive.booking.model.DeletedOutbox;
import com.autohubreactive.booking.producer.bookingprocessing.DeletedBookingProducerService;
import com.autohubreactive.booking.repository.BookingRepository;
import com.autohubreactive.booking.repository.DeletedOutboxRepository;
import com.autohubreactive.booking.util.TestUtil;
import com.autohubreactive.model.Booking;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeletedOutboxServiceTest {

    @InjectMocks
    private DeletedOutboxService deletedOutboxService;

    @Mock
    private DeletedOutboxRepository deletedOutboxRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private DeletedBookingProducerService deletedBookingProducerService;

    @Test
    void handleOutboxesTest_success() {
        DeletedOutbox deletedOutbox = TestUtil.getResourceAsJson("/data/DeletedOutbox.json", DeletedOutbox.class);

        when(deletedOutboxRepository.findAll()).thenReturn(Flux.just(deletedOutbox));
        when(deletedBookingProducerService.sendMessage(anyString())).thenReturn(Mono.empty());
        when(deletedOutboxRepository.delete(deletedOutbox)).thenReturn(Mono.empty());

        deletedOutboxService.handleOutboxes()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void handleOutboxesTest_errorOnBookingIdSend() {
        DeletedOutbox deletedOutbox = TestUtil.getResourceAsJson("/data/DeletedOutbox.json", DeletedOutbox.class);

        when(deletedOutboxRepository.findAll()).thenReturn(Flux.just(deletedOutbox));
        when(deletedBookingProducerService.sendMessage(anyString())).thenReturn(Mono.error(new RuntimeException("Test")));

        deletedOutboxService.handleOutboxes()
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void processBookingDeletionTest_success() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);
        DeletedOutbox deletedOutbox = TestUtil.getResourceAsJson("/data/DeletedOutbox.json", DeletedOutbox.class);

        when(bookingRepository.deleteById(any(ObjectId.class))).thenReturn(Mono.empty());
        when(deletedOutboxRepository.save(any(DeletedOutbox.class))).thenReturn(Mono.just(deletedOutbox));

        deletedOutboxService.processBookingDeletion(booking)
                .as(StepVerifier::create)
                .assertNext(actualOutbox -> assertThat(actualOutbox).usingRecursiveComparison().isEqualTo(deletedOutbox))
                .verifyComplete();
    }

}
