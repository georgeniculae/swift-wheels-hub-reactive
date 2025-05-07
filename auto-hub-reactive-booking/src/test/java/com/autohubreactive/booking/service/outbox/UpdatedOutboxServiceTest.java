package com.autohubreactive.booking.service.outbox;

import com.autohubreactive.booking.mapper.BookingMapper;
import com.autohubreactive.booking.mapper.BookingMapperImpl;
import com.autohubreactive.booking.model.UpdatedOutbox;
import com.autohubreactive.booking.producer.bookingprocessing.UpdatedBookingProducerService;
import com.autohubreactive.booking.producer.bookingprocessing.UpdatedBookingUpdateCarsProducerService;
import com.autohubreactive.booking.producer.dlq.FailedUpdatedBookingDlqProducerService;
import com.autohubreactive.booking.repository.BookingRepository;
import com.autohubreactive.booking.repository.UpdateOutboxRepository;
import com.autohubreactive.booking.util.TestUtil;
import com.autohubreactive.dto.common.BookingResponse;
import com.autohubreactive.dto.common.UpdateCarsRequest;
import com.autohubreactive.dto.booking.UpdatedBookingReprocessRequest;
import com.autohubreactive.model.booking.Booking;
import org.bson.types.ObjectId;
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
class UpdatedOutboxServiceTest {

    @InjectMocks
    private UpdatedOutboxService updatedOutboxService;

    @Mock
    private UpdateOutboxRepository updateOutboxRepository;

    @Mock
    private UpdatedBookingProducerService updatedBookingProducerService;

    @Mock
    private UpdatedBookingUpdateCarsProducerService updatedBookingUpdateCarsProducerService;

    @Mock
    private FailedUpdatedBookingDlqProducerService failedUpdatedBookingDlqProducerService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ReactiveRedisOperations<String, String> redisOperations;

    @Mock
    private ReactiveValueOperations<String, String> reactiveValueOperations;

    @Spy
    private BookingMapper bookingMapper = new BookingMapperImpl();

    @Test
    void handleOutboxesTest_success() {
        UpdatedOutbox updatedOutbox =
                TestUtil.getResourceAsJson("/data/UpdatedOutbox.json", UpdatedOutbox.class);

        when(updateOutboxRepository.findAll()).thenReturn(Flux.just(updatedOutbox));
        when(updatedBookingProducerService.sendBookingResponse(any(BookingResponse.class))).thenReturn(Mono.empty());
        when(redisOperations.opsForValue()).thenReturn(reactiveValueOperations);
        when(reactiveValueOperations.delete(anyString())).thenReturn(Mono.just(true));
        when(updateOutboxRepository.delete(any(UpdatedOutbox.class))).thenReturn(Mono.empty());

        updatedOutboxService.handleOutboxes()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        verify(bookingMapper).mapEntityToDto(any(Booking.class));
    }

    @Test
    void handleOutboxesTest_carNotChangedFailed() {
        UpdatedOutbox updatedOutbox =
                TestUtil.getResourceAsJson("/data/UpdatedOutbox.json", UpdatedOutbox.class);

        when(updateOutboxRepository.findAll()).thenReturn(Flux.just(updatedOutbox));
        when(updatedBookingProducerService.sendBookingResponse(any(BookingResponse.class))).thenReturn(Mono.empty());
        when(failedUpdatedBookingDlqProducerService.sendUpdatedBookingReprocessRequest(any(UpdatedBookingReprocessRequest.class)))
                .thenReturn(Mono.empty());
        when(updateOutboxRepository.delete(any(UpdatedOutbox.class))).thenReturn(Mono.empty());

        updatedOutboxService.handleOutboxes()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        verify(bookingMapper).mapEntityToDto(any(Booking.class));
    }

    @Test
    void handleOutboxesTest_carChanged_success() {
        UpdatedOutbox updatedOutbox =
                TestUtil.getResourceAsJson("/data/UpdatedOutboxCarChanged.json", UpdatedOutbox.class);

        when(updateOutboxRepository.findAll()).thenReturn(Flux.just(updatedOutbox));
        when(updatedBookingUpdateCarsProducerService.sendUpdateCarsRequest(any(UpdateCarsRequest.class)))
                .thenReturn(Mono.empty());
        when(updatedBookingProducerService.sendBookingResponse(any(BookingResponse.class))).thenReturn(Mono.empty());
        when(redisOperations.opsForValue()).thenReturn(reactiveValueOperations);
        when(reactiveValueOperations.delete(anyString())).thenReturn(Mono.just(true));
        when(updateOutboxRepository.delete(any(UpdatedOutbox.class))).thenReturn(Mono.empty());

        updatedOutboxService.handleOutboxes()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        verify(bookingMapper).mapEntityToDto(any(Booking.class));
    }

    @Test
    void handleOutboxesTest_bookingUpdateFailed() {
        UpdatedOutbox updatedOutbox =
                TestUtil.getResourceAsJson("/data/UpdatedOutboxCarChanged.json", UpdatedOutbox.class);

        when(updateOutboxRepository.findAll()).thenReturn(Flux.just(updatedOutbox));
        when(updatedBookingUpdateCarsProducerService.sendUpdateCarsRequest(any(UpdateCarsRequest.class)))
                .thenReturn(Mono.empty());
        when(updatedBookingProducerService.sendBookingResponse(any(BookingResponse.class))).thenReturn(Mono.empty());
        when(failedUpdatedBookingDlqProducerService.sendUpdatedBookingReprocessRequest(any(UpdatedBookingReprocessRequest.class)))
                .thenReturn(Mono.empty());
        when(updateOutboxRepository.delete(any(UpdatedOutbox.class))).thenReturn(Mono.empty());

        updatedOutboxService.handleOutboxes()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        verify(bookingMapper).mapEntityToDto(any(Booking.class));
    }

    @Test
    void handleOutboxesTest_bookingUpdateError() {
        UpdatedOutbox updatedOutbox =
                TestUtil.getResourceAsJson("/data/UpdatedOutboxCarChanged.json", UpdatedOutbox.class);

        when(updateOutboxRepository.findAll()).thenReturn(Flux.just(updatedOutbox));
        when(updatedBookingUpdateCarsProducerService.sendUpdateCarsRequest(any(UpdateCarsRequest.class)))
                .thenReturn(Mono.empty());
        when(updatedBookingProducerService.sendBookingResponse(any(BookingResponse.class)))
                .thenReturn(Mono.error(new RuntimeException()));
        when(failedUpdatedBookingDlqProducerService.sendUpdatedBookingReprocessRequest(any(UpdatedBookingReprocessRequest.class)))
                .thenReturn(Mono.empty());
        when(updateOutboxRepository.delete(any(UpdatedOutbox.class))).thenReturn(Mono.empty());

        updatedOutboxService.handleOutboxes()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        verify(bookingMapper).mapEntityToDto(any(Booking.class));
    }

    @Test
    void handleOutboxesTest_errorOnFindingAll() {
        when(updateOutboxRepository.findAll()).thenReturn(Flux.error(new RuntimeException()));

        updatedOutboxService.handleOutboxes()
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void processBookingUpdateTest_success() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);
        booking.setPreviousCarId(new ObjectId("64f361caf291ae086e179547"));
        UpdatedOutbox updatedOutbox = TestUtil.getResourceAsJson("/data/UpdatedOutbox.json", UpdatedOutbox.class);

        when(bookingRepository.save(any(Booking.class))).thenReturn(Mono.just(booking));
        when(updateOutboxRepository.save(any(UpdatedOutbox.class))).thenReturn(Mono.just(updatedOutbox));

        updatedOutboxService.processBookingUpdate(booking)
                .as(StepVerifier::create)
                .assertNext(actualBooking -> assertThat(actualBooking).usingRecursiveComparison().isEqualTo(booking))
                .verifyComplete();
    }

}
