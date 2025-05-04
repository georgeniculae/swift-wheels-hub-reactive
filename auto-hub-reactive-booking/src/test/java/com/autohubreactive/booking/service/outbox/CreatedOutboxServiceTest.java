package com.autohubreactive.booking.service.outbox;

import com.autohubreactive.booking.mapper.BookingMapper;
import com.autohubreactive.booking.mapper.BookingMapperImpl;
import com.autohubreactive.booking.model.CreatedOutbox;
import com.autohubreactive.booking.producer.bookingprocessing.CreatedBookingCarUpdateProducerService;
import com.autohubreactive.booking.producer.bookingprocessing.CreatedBookingProducerService;
import com.autohubreactive.booking.producer.dlq.FailedCreatedBookingDlqProducerService;
import com.autohubreactive.booking.repository.BookingRepository;
import com.autohubreactive.booking.repository.CreatedOutboxRepository;
import com.autohubreactive.booking.util.TestUtil;
import com.autohubreactive.dto.BookingResponse;
import com.autohubreactive.dto.CarStatusUpdate;
import com.autohubreactive.dto.CreatedBookingReprocessRequest;
import com.autohubreactive.model.booking.Booking;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreatedOutboxServiceTest {

    @InjectMocks
    private CreatedOutboxService createdOutboxService;

    @Mock
    private CreatedOutboxRepository createdOutboxRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CreatedBookingProducerService createdBookingProducerService;

    @Mock
    private CreatedBookingCarUpdateProducerService createdBookingCarUpdateProducerService;

    @Mock
    private FailedCreatedBookingDlqProducerService failedCreatedBookingDlqProducerService;

    @Mock
    private ReactiveRedisOperations<String, String> redisOperations;

    @Mock
    private ReactiveValueOperations<String, String> reactiveValueOperations;

    @Spy
    private BookingMapper bookingMapper = new BookingMapperImpl();

    @Test
    void handleOutboxesTest_success() {
        CreatedOutbox createdOutbox =
                TestUtil.getResourceAsJson("/data/CreatedOutbox.json", CreatedOutbox.class);

        when(createdOutboxRepository.findAll()).thenReturn(Flux.just(createdOutbox));
        when(createdBookingCarUpdateProducerService.sendCarUpdateDetails(any(CarStatusUpdate.class)))
                .thenReturn(Mono.empty());
        when(createdBookingProducerService.sendCreatedBooking(any(BookingResponse.class))).thenReturn(Mono.empty());
        when(redisOperations.opsForValue()).thenReturn(reactiveValueOperations);
        when(reactiveValueOperations.delete(anyString())).thenReturn(Mono.just(true));
        when(createdOutboxRepository.delete(any(CreatedOutbox.class))).thenReturn(Mono.empty());

        createdOutboxService.handleOutboxes()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        verify(bookingMapper).mapEntityToDto(any(Booking.class));
    }

    @Test
    void handleOutboxesTest_errorOnUpdatingCar() {
        CreatedOutbox createdOutbox =
                TestUtil.getResourceAsJson("/data/CreatedOutbox.json", CreatedOutbox.class);

        when(createdOutboxRepository.findAll()).thenReturn(Flux.just(createdOutbox));
        when(createdBookingCarUpdateProducerService.sendCarUpdateDetails(any(CarStatusUpdate.class)))
                .thenReturn(Mono.error(new RuntimeException("Test")));
        when(failedCreatedBookingDlqProducerService.sendCreatedBookingReprocessRequest(any(CreatedBookingReprocessRequest.class)))
                .thenReturn(Mono.empty());
        when(createdOutboxRepository.delete(any(CreatedOutbox.class))).thenReturn(Mono.empty());

        createdOutboxService.handleOutboxes()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void handleOutboxesTest_errorOnSendingBooking() {
        CreatedOutbox createdOutbox =
                TestUtil.getResourceAsJson("/data/CreatedOutbox.json", CreatedOutbox.class);

        when(createdOutboxRepository.findAll()).thenReturn(Flux.just(createdOutbox));
        when(createdBookingCarUpdateProducerService.sendCarUpdateDetails(any(CarStatusUpdate.class)))
                .thenReturn(Mono.empty());
        when(redisOperations.opsForValue()).thenReturn(reactiveValueOperations);
        when(reactiveValueOperations.delete(anyString())).thenReturn(Mono.just(true));
        when(createdBookingProducerService.sendCreatedBooking(any(BookingResponse.class)))
                .thenReturn(Mono.error(new RuntimeException("Test")));
        when(failedCreatedBookingDlqProducerService.sendCreatedBookingReprocessRequest(any(CreatedBookingReprocessRequest.class)))
                .thenReturn(Mono.empty());
        when(createdOutboxRepository.delete(any(CreatedOutbox.class))).thenReturn(Mono.empty());

        createdOutboxService.handleOutboxes()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void handleOutboxesTest_errorOnFindingOutboxes() {
        when(createdOutboxRepository.findAll()).thenReturn(Flux.error(new RuntimeException("Test")));

        createdOutboxService.handleOutboxes()
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void processBookingSaveTest_success() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);

        CreatedOutbox createdOutbox = TestUtil.getResourceAsJson("/data/CreatedOutbox.json", CreatedOutbox.class);

        when(bookingRepository.save(any(Booking.class))).thenReturn(Mono.just(booking));
        when(createdOutboxRepository.save(any(CreatedOutbox.class))).thenReturn(Mono.just(createdOutbox));

        createdOutboxService.processBookingSave(booking)
                .as(StepVerifier::create)
                .assertNext(actualBooking -> assertThat(actualBooking).usingRecursiveComparison().isEqualTo(booking))
                .verifyComplete();
    }

}
