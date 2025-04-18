package com.swiftwheelshubreactive.booking.service.bookingprocessing;

import com.swiftwheelshubreactive.booking.mapper.BookingMapper;
import com.swiftwheelshubreactive.booking.mapper.BookingMapperImpl;
import com.swiftwheelshubreactive.booking.model.CreatedOutbox;
import com.swiftwheelshubreactive.booking.repository.BookingRepository;
import com.swiftwheelshubreactive.booking.service.outbox.CreatedOutboxService;
import com.swiftwheelshubreactive.booking.service.outbox.DeletedOutboxService;
import com.swiftwheelshubreactive.booking.service.outbox.UpdatedOutboxService;
import com.swiftwheelshubreactive.booking.util.TestUtil;
import com.swiftwheelshubreactive.dto.AuthenticationInfo;
import com.swiftwheelshubreactive.dto.AvailableCarInfo;
import com.swiftwheelshubreactive.dto.BookingClosingDetails;
import com.swiftwheelshubreactive.dto.BookingRequest;
import com.swiftwheelshubreactive.dto.BookingResponse;
import com.swiftwheelshubreactive.model.Booking;
import com.swiftwheelshubreactive.model.BookingStatus;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @InjectMocks
    private BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CarService carService;

    @Mock
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @Mock
    private CreatedOutboxService createdOutboxService;

    @Mock
    private UpdatedOutboxService updatedOutboxService;

    @Mock
    private DeletedOutboxService deletedOutboxService;

    @Mock
    private ReactiveRedisOperations<String, String> redisOperations;

    @Mock
    private ReactiveValueOperations<String, String> reactiveValueOperations;

    @Spy
    private BookingMapper bookingMapper = new BookingMapperImpl();

    @Test
    void findAllBookingTest_success() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);

        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        when(bookingRepository.findAll()).thenReturn(Flux.just(booking));

        StepVerifier.create(bookingService.findAllBookings())
                .expectNext(bookingResponse)
                .verifyComplete();
    }

    @Test
    void findAllBookingTest_errorOnFindingAll() {
        when(bookingRepository.findAll()).thenReturn(Flux.error(new Throwable()));

        StepVerifier.create(bookingService.findAllBookings())
                .expectError()
                .verify();
    }

    @Test
    void findBookingByIdTest_success() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);
        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);
        when(bookingRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(booking));

        StepVerifier.create(bookingService.findBookingById("64f361caf291ae086e179547"))
                .expectNext(bookingResponse)
                .verifyComplete();
    }

    @Test
    void findBookingsByLoggedInUserTest_success() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);
        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        when(bookingRepository.findByCustomerUsername(anyString())).thenReturn(Flux.just(booking));

        StepVerifier.create(bookingService.findBookingsByLoggedInUser("admin"))
                .expectNext(bookingResponse)
                .verifyComplete();
    }

    @Test
    void findBookingsByLoggedInUserTest_errorOnFindingBookingByCustomerUsername() {
        when(bookingRepository.findByCustomerUsername(anyString())).thenReturn(Flux.error(new Throwable()));

        StepVerifier.create(bookingService.findBookingsByLoggedInUser("admin"))
                .expectError()
                .verify();
    }

    @Test
    void countBookingsTest_success() {
        when(bookingRepository.count()).thenReturn(Mono.just(3L));

        StepVerifier.create(bookingService.countBookings())
                .expectNext(3L)
                .verifyComplete();
    }

    @Test
    void countBookingsTest_errorOnCount() {
        when(bookingRepository.count()).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(bookingService.countBookings())
                .expectError()
                .verify();
    }

    @Test
    void countBookingsOfLoggedInUserTest_success() {
        when(bookingRepository.countByCustomerUsername(anyString())).thenReturn(Mono.just(3L));

        StepVerifier.create(bookingService.countBookingsOfLoggedInUser("admin"))
                .expectNext(3L)
                .verifyComplete();
    }

    @Test
    void countBookingsOfLoggedInUserTest_errorOnCountingByCustomerUsername() {
        when(bookingRepository.countByCustomerUsername(anyString())).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(bookingService.countBookingsOfLoggedInUser("admin"))
                .expectError()
                .verify();
    }

    @Test
    void getCurrentDateTest_success() {
        StepVerifier.create(bookingService.getCurrentDate())
                .expectNext(LocalDate.now())
                .verifyComplete();
    }

    @Test
    void findBookingByIdTest_errorOnFindingById() {
        when(bookingRepository.findById(any(ObjectId.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(bookingService.findBookingById("64f361caf291ae086e179547"))
                .expectError()
                .verify();
    }

    @Test
    void saveBookingTest_success() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);

        BookingRequest bookingRequest =
                TestUtil.getResourceAsJson("/data/BookingRequest.json", BookingRequest.class);

        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        AvailableCarInfo availableCarInfo =
                TestUtil.getResourceAsJson("/data/AvailableCarInfo.json", AvailableCarInfo.class);

        String apikey = "apikey";

        AuthenticationInfo authenticationInfo = AuthenticationInfo.builder()
                .apikey(apikey)
                .username("user")
                .email("user@mail.com")
                .roles(List.of("admin"))
                .build();

        when(redisOperations.opsForValue()).thenReturn(reactiveValueOperations);
        when(reactiveValueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenReturn(Mono.just(true));
        when(carService.findAvailableCarById(any(AuthenticationInfo.class), anyString())).thenReturn(Mono.just(availableCarInfo));
        when(createdOutboxService.processBookingSave(any(Booking.class))).thenReturn(Mono.just(booking));

        bookingService.saveBooking(authenticationInfo, bookingRequest)
                .as(StepVerifier::create)
                .assertNext(actualBookingResponse -> assertThat(actualBookingResponse).usingRecursiveComparison().isEqualTo(bookingResponse))
                .verifyComplete();
    }

    @Test
    void saveBookingTest_failedCarLock() {
        BookingRequest bookingRequest =
                TestUtil.getResourceAsJson("/data/BookingRequest.json", BookingRequest.class);

        String apikey = "apikey";

        AuthenticationInfo authenticationInfo = AuthenticationInfo.builder()
                .apikey(apikey)
                .roles(List.of("admin"))
                .build();

        when(redisOperations.opsForValue()).thenReturn(reactiveValueOperations);
        when(reactiveValueOperations.setIfAbsent(anyString(), anyString())).thenReturn(Mono.just(true));

        bookingService.saveBooking(authenticationInfo, bookingRequest)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void saveBookingTest_errorOnFindingAvailableCarById() {
        BookingRequest bookingRequest =
                TestUtil.getResourceAsJson("/data/BookingRequest.json", BookingRequest.class);

        String apikey = "apikey";

        AuthenticationInfo authenticationInfo = AuthenticationInfo.builder()
                .apikey(apikey)
                .roles(List.of("admin"))
                .build();

        when(redisOperations.opsForValue()).thenReturn(reactiveValueOperations);
        when(reactiveValueOperations.setIfAbsent(anyString(), anyString())).thenReturn(Mono.just(false));
        when(carService.findAvailableCarById(any(AuthenticationInfo.class), anyString())).thenReturn(Mono.error(new Throwable()));

        bookingService.saveBooking(authenticationInfo, bookingRequest)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void closeBookingTest_success() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);

        Booking updatedClosedBooking =
                TestUtil.getResourceAsJson("/data/UpdatedClosedBooking.json", Booking.class);

        BookingClosingDetails bookingClosingDetails =
                TestUtil.getResourceAsJson("/data/BookingClosingDetails.json", BookingClosingDetails.class);

        when(bookingRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(Mono.just(updatedClosedBooking));

        StepVerifier.create(bookingService.closeBooking(bookingClosingDetails))
                .expectComplete()
                .verify();
    }

    @Test
    void closeBookingTest_errorOnSavingCar() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);

        BookingClosingDetails bookingClosingDetails =
                TestUtil.getResourceAsJson("/data/BookingClosingDetails.json", BookingClosingDetails.class);

        when(bookingRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(bookingService.closeBooking(bookingClosingDetails))
                .expectError()
                .verify();
    }

    @Test
    void updateBookingTest_success() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);

        BookingRequest bookingRequest =
                TestUtil.getResourceAsJson("/data/BookingRequest.json", BookingRequest.class);

        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        String apikey = "apikey";

        AuthenticationInfo authenticationInfo = AuthenticationInfo.builder()
                .apikey(apikey)
                .roles(List.of("admin"))
                .build();

        when(bookingRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(booking));
        when(updatedOutboxService.processBookingUpdate(any(Booking.class))).thenReturn(Mono.just(booking));

        bookingService.updateBooking(authenticationInfo, "64f361caf291ae086e179547", bookingRequest)
                .as(StepVerifier::create)
                .assertNext(actualBookingResponse -> assertThat(actualBookingResponse).usingRecursiveComparison().isEqualTo(bookingResponse))
                .verifyComplete();

        verify(carService, never()).findAvailableCarById(any(AuthenticationInfo.class), anyString());
    }

    @Test
    void updateBookingTest_errorOnFindingBookingById() {
        BookingRequest bookingRequest =
                TestUtil.getResourceAsJson("/data/BookingRequest.json", BookingRequest.class);
        String apikey = "apikey";
        AuthenticationInfo authenticationInfo = AuthenticationInfo.builder()
                .apikey(apikey)
                .roles(List.of("admin"))
                .build();

        when(bookingRepository.findById(any(ObjectId.class))).thenReturn(Mono.error(new Throwable()));

        bookingService.updateBooking(authenticationInfo, "64f361caf291ae086e179547", bookingRequest)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void updateBookingTest_updatedCar_success() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);

        BookingRequest updatedBookingRequest =
                TestUtil.getResourceAsJson("/data/UpdatedBookingRequest.json", BookingRequest.class);

        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        AvailableCarInfo availableCarInfo =
                TestUtil.getResourceAsJson("/data/AvailableCarInfo.json", AvailableCarInfo.class);

        CreatedOutbox outbox = TestUtil.getResourceAsJson("/data/CreatedOutbox.json", CreatedOutbox.class);

        outbox.getContent().setActualCarId(new ObjectId("64f361caf291ae086e179222"));

        String apikey = "apikey";

        AuthenticationInfo authenticationInfo = AuthenticationInfo.builder()
                .apikey(apikey)
                .roles(List.of("admin"))
                .build();

        when(redisOperations.opsForValue()).thenReturn(reactiveValueOperations);
        when(reactiveValueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class)))
                .thenReturn(Mono.just(true));
        when(carService.findAvailableCarById(any(AuthenticationInfo.class), anyString()))
                .thenReturn(Mono.just(availableCarInfo));
        when(bookingRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(booking));
        when(updatedOutboxService.processBookingUpdate(any(Booking.class))).thenReturn(Mono.just(booking));

        bookingService.updateBooking(authenticationInfo, "64f361caf291ae086e179547", updatedBookingRequest)
                .as(StepVerifier::create)
                .assertNext(actualBookingResponse -> assertThat(actualBookingResponse).usingRecursiveComparison().isEqualTo(bookingResponse))
                .verifyComplete();
    }

    @Test
    void updateBookingTest_updateCar_failedCarLock() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);

        BookingRequest updatedBookingRequest =
                TestUtil.getResourceAsJson("/data/UpdatedBookingRequest.json", BookingRequest.class);

        AvailableCarInfo availableCarInfo =
                TestUtil.getResourceAsJson("/data/AvailableCarInfo.json", AvailableCarInfo.class);

        CreatedOutbox outbox = TestUtil.getResourceAsJson("/data/CreatedOutbox.json", CreatedOutbox.class);

        outbox.getContent().setActualCarId(new ObjectId("64f361caf291ae086e179222"));

        String apikey = "apikey";

        AuthenticationInfo authenticationInfo = AuthenticationInfo.builder()
                .apikey(apikey)
                .roles(List.of("admin"))
                .build();

        when(bookingRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(booking));
        when(redisOperations.opsForValue()).thenReturn(reactiveValueOperations);
        when(reactiveValueOperations.setIfAbsent(anyString(), anyString())).thenReturn(Mono.just(true));
        when(carService.findAvailableCarById(any(AuthenticationInfo.class), anyString()))
                .thenReturn(Mono.just(availableCarInfo));

        bookingService.updateBooking(authenticationInfo, "64f361caf291ae086e179547", updatedBookingRequest)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void findBookingByDateOfBookingTest_success() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);

        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        when(reactiveMongoTemplate.find(any(Query.class), eq(Booking.class))).thenReturn(Flux.just(booking));

        StepVerifier.create(bookingService.findBookingsByDateOfBooking("2099-02-20"))
                .expectNext(bookingResponse)
                .verifyComplete();
    }

    @Test
    void findBookingByDateOfBookingTest_errorOnFindingByDateOfBooking() {
        when(reactiveMongoTemplate.find(any(Query.class), eq(Booking.class))).thenReturn(Flux.error(new Throwable()));

        StepVerifier.create(bookingService.findBookingsByDateOfBooking("2099-02-20"))
                .expectError()
                .verify();
    }

    @Test
    void deleteBookingByCustomerUsernameTest_success() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);
        booking.setStatus(BookingStatus.CLOSED);

        when(bookingRepository.existsByCustomerUsernameAndStatus(anyString(), any(BookingStatus.class)))
                .thenReturn(Mono.just(false));
        when(bookingRepository.findByCustomerUsername(anyString())).thenReturn(Flux.just(booking));
        when(deletedOutboxService.processBookingDeletion(any(Booking.class))).thenReturn(Mono.empty());

        bookingService.deleteBookingByCustomerUsername("user")
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void deleteBookingByCustomerUsernameTest_bookingInProgress_error() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);
        booking.setStatus(BookingStatus.CLOSED);

        when(bookingRepository.existsByCustomerUsernameAndStatus(anyString(), any(BookingStatus.class)))
                .thenReturn(Mono.just(true));

        bookingService.deleteBookingByCustomerUsername("user")
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void deleteBookingByCustomerUsernameTest_errorOnFindingById() {
        when(bookingRepository.existsByCustomerUsernameAndStatus(anyString(), any(BookingStatus.class)))
                .thenReturn(Mono.just(false));
        when(bookingRepository.findByCustomerUsername(anyString())).thenReturn(Flux.error(new RuntimeException()));

        bookingService.deleteBookingByCustomerUsername("user")
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

}
