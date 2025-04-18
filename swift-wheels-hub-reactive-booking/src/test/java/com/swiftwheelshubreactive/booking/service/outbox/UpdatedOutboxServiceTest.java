package com.swiftwheelshubreactive.booking.service.outbox;

import com.swiftwheelshubreactive.booking.mapper.BookingMapper;
import com.swiftwheelshubreactive.booking.mapper.BookingMapperImpl;
import com.swiftwheelshubreactive.booking.model.UpdatedOutbox;
import com.swiftwheelshubreactive.booking.producer.bookingprocessing.UpdatedBookingProducerService;
import com.swiftwheelshubreactive.booking.producer.bookingprocessing.UpdatedBookingUpdateCarsProducerService;
import com.swiftwheelshubreactive.booking.producer.dlq.FailedUpdatedBookingDlqProducerService;
import com.swiftwheelshubreactive.booking.repository.BookingRepository;
import com.swiftwheelshubreactive.booking.repository.UpdateOutboxRepository;
import com.swiftwheelshubreactive.booking.util.TestUtil;
import com.swiftwheelshubreactive.dto.BookingResponse;
import com.swiftwheelshubreactive.dto.UpdateCarsRequest;
import com.swiftwheelshubreactive.dto.UpdatedBookingReprocessRequest;
import com.swiftwheelshubreactive.model.Booking;
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
import static org.mockito.Mockito.never;
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
        when(updatedBookingProducerService.sengBookingResponse(any(BookingResponse.class))).thenReturn(Mono.just(true));
        when(redisOperations.opsForValue()).thenReturn(reactiveValueOperations);
        when(reactiveValueOperations.delete(anyString())).thenReturn(Mono.just(true));
        when(updateOutboxRepository.delete(any(UpdatedOutbox.class))).thenReturn(Mono.empty());

        StepVerifier.create(updatedOutboxService.handleOutboxes())
                .expectComplete()
                .verify();

        verify(bookingMapper).mapEntityToDto(any(Booking.class));
    }

    @Test
    void handleOutboxesTest_carNotChangedFailed() {
        UpdatedOutbox updatedOutbox =
                TestUtil.getResourceAsJson("/data/UpdatedOutbox.json", UpdatedOutbox.class);

        when(updateOutboxRepository.findAll()).thenReturn(Flux.just(updatedOutbox));
        when(updatedBookingProducerService.sengBookingResponse(any(BookingResponse.class))).thenReturn(Mono.just(false));
        when(failedUpdatedBookingDlqProducerService.sendUpdatedBookingReprocessrequest(any(UpdatedBookingReprocessRequest.class)))
                .thenReturn(Mono.empty());
        when(updateOutboxRepository.delete(any(UpdatedOutbox.class))).thenReturn(Mono.empty());

        StepVerifier.create(updatedOutboxService.handleOutboxes())
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
                .thenReturn(Mono.just(true));
        when(updatedBookingProducerService.sengBookingResponse(any(BookingResponse.class))).thenReturn(Mono.just(true));
        when(redisOperations.opsForValue()).thenReturn(reactiveValueOperations);
        when(reactiveValueOperations.delete(anyString())).thenReturn(Mono.just(true));
        when(updateOutboxRepository.delete(any(UpdatedOutbox.class))).thenReturn(Mono.empty());

        StepVerifier.create(updatedOutboxService.handleOutboxes())
                .expectComplete()
                .verify();

        verify(bookingMapper).mapEntityToDto(any(Booking.class));
    }

    @Test
    void handleOutboxesTest_carChangeFailed() {
        UpdatedOutbox updatedOutbox =
                TestUtil.getResourceAsJson("/data/UpdatedOutboxCarChanged.json", UpdatedOutbox.class);

        when(updateOutboxRepository.findAll()).thenReturn(Flux.just(updatedOutbox));
        when(updatedBookingUpdateCarsProducerService.sendUpdateCarsRequest(any(UpdateCarsRequest.class)))
                .thenReturn(Mono.just(false));
        when(failedUpdatedBookingDlqProducerService.sendUpdatedBookingReprocessrequest(any(UpdatedBookingReprocessRequest.class)))
                .thenReturn(Mono.empty());
        when(updateOutboxRepository.delete(any(UpdatedOutbox.class))).thenReturn(Mono.empty());

        StepVerifier.create(updatedOutboxService.handleOutboxes())
                .expectComplete()
                .verify();

        verify(bookingMapper, never()).mapEntityToDto(any(Booking.class));
    }

    @Test
    void handleOutboxesTest_bookingUpdateFailed() {
        UpdatedOutbox updatedOutbox =
                TestUtil.getResourceAsJson("/data/UpdatedOutboxCarChanged.json", UpdatedOutbox.class);

        when(updateOutboxRepository.findAll()).thenReturn(Flux.just(updatedOutbox));
        when(updatedBookingUpdateCarsProducerService.sendUpdateCarsRequest(any(UpdateCarsRequest.class)))
                .thenReturn(Mono.just(true));
        when(updatedBookingProducerService.sengBookingResponse(any(BookingResponse.class))).thenReturn(Mono.just(false));
        when(failedUpdatedBookingDlqProducerService.sendUpdatedBookingReprocessrequest(any(UpdatedBookingReprocessRequest.class)))
                .thenReturn(Mono.empty());
        when(updateOutboxRepository.delete(any(UpdatedOutbox.class))).thenReturn(Mono.empty());

        StepVerifier.create(updatedOutboxService.handleOutboxes())
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
                .thenReturn(Mono.just(true));
        when(updatedBookingProducerService.sengBookingResponse(any(BookingResponse.class)))
                .thenReturn(Mono.error(new RuntimeException()));
        when(failedUpdatedBookingDlqProducerService.sendUpdatedBookingReprocessrequest(any(UpdatedBookingReprocessRequest.class)))
                .thenReturn(Mono.empty());
        when(updateOutboxRepository.delete(any(UpdatedOutbox.class))).thenReturn(Mono.empty());

        StepVerifier.create(updatedOutboxService.handleOutboxes())
                .expectComplete()
                .verify();

        verify(bookingMapper).mapEntityToDto(any(Booking.class));
    }

    @Test
    void handleOutboxesTest_errorOnFindingAll() {
        when(updateOutboxRepository.findAll()).thenReturn(Flux.error(new RuntimeException()));

        StepVerifier.create(updatedOutboxService.handleOutboxes())
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
