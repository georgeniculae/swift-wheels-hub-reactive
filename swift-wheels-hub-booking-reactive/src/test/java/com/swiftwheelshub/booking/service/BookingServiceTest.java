package com.swiftwheelshub.booking.service;

import com.swiftwheelshub.booking.mapper.BookingMapper;
import com.swiftwheelshub.booking.mapper.BookingMapperImpl;
import com.swiftwheelshub.booking.model.Outbox;
import com.swiftwheelshub.booking.repository.BookingRepository;
import com.swiftwheelshub.booking.util.TestUtils;
import com.swiftwheelshub.dto.BookingClosingDetails;
import com.swiftwheelshub.dto.BookingRequest;
import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.dto.CarResponse;
import com.swiftwheelshub.dto.CarState;
import com.swiftwheelshub.dto.CarUpdateDetails;
import com.swiftwheelshub.dto.EmployeeResponse;
import com.swiftwheelshub.model.Booking;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
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
    private OutboxService outboxService;

    @Mock
    private EmployeeService employeeService;

    @Spy
    private BookingMapper bookingMapper = new BookingMapperImpl();

    @Test
    void findAllBookingTest_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);
        BookingResponse bookingResponse =
                TestUtils.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

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
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);
        BookingResponse bookingResponse =
                TestUtils.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);
        when(bookingRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(booking));

        StepVerifier.create(bookingService.findBookingById("64f361caf291ae086e179547"))
                .expectNext(bookingResponse)
                .verifyComplete();
    }

    @Test
    void findBookingsByLoggedInUserTest_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);
        BookingResponse bookingResponse =
                TestUtils.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

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
        BookingRequest bookingRequest =
                TestUtils.getResourceAsJson("/data/BookingRequest.json", BookingRequest.class);
        BookingResponse bookingResponse =
                TestUtils.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);
        Outbox outbox = TestUtils.getResourceAsJson("/data/Outbox.json", Outbox.class);
        String apikey = "token";

        when(carService.findAvailableCarById(anyString(), anyList(), anyString())).thenReturn(Mono.just(carResponse));
        when(outboxService.saveBookingAndOutboxTransactional(any(Booking.class), any(Outbox.Operation.class)))
                .thenReturn(Mono.just(outbox));
        when(carService.changeCarStatus(anyString(), anyList(), anyString(), any(CarState.class))).thenReturn(Mono.just(carResponse));

        StepVerifier.create(bookingService.saveBooking(apikey, List.of("admin"), bookingRequest))
                .expectNext(bookingResponse)
                .verifyComplete();

        verify(bookingMapper, times(1)).mapEntityToDto(any(Booking.class));
    }

    @Test
    void saveBookingTest_errorOnFindingAvailableCarById() {
        BookingRequest bookingRequest =
                TestUtils.getResourceAsJson("/data/BookingRequest.json", BookingRequest.class);
        String apikey = "token";

        when(carService.findAvailableCarById(anyString(), anyList(), anyString())).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(bookingService.saveBooking(apikey, List.of("admin"), bookingRequest))
                .expectError()
                .verify();
    }

    @Test
    void closeBookingTest_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);
        BookingResponse closedBookingResponse =
                TestUtils.getResourceAsJson("/data/ClosedBookingResponse.json", BookingResponse.class);
        BookingClosingDetails bookingClosingDetails =
                TestUtils.getResourceAsJson("/data/BookingClosingDetails.json", BookingClosingDetails.class);
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);
        EmployeeResponse employeeResponse =
                TestUtils.getResourceAsJson("/data/EmployeeResponse.json", EmployeeResponse.class);
        String apikey = "apikey";

        when(bookingRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(Mono.just(booking));
        when(employeeService.findEmployeeById(anyString(), anyList(), anyString())).thenReturn(Mono.just(employeeResponse));
        when(carService.updateCarWhenBookingIsFinished(anyString(), anyList(), any(CarUpdateDetails.class)))
                .thenReturn(Mono.just(carResponse));

        StepVerifier.create(bookingService.closeBooking(apikey, List.of("admin"), bookingClosingDetails))
                .expectNext(closedBookingResponse)
                .verifyComplete();
    }

    @Test
    void closeBookingTest_errorOnUpdatingCarWhenBookingIsFinished() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);
        BookingClosingDetails bookingClosingDetails =
                TestUtils.getResourceAsJson("/data/BookingClosingDetails.json", BookingClosingDetails.class);
        EmployeeResponse employeeResponse =
                TestUtils.getResourceAsJson("/data/EmployeeResponse.json", EmployeeResponse.class);
        String apikeySecret = "token";

        when(bookingRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(Mono.just(booking));
        when(employeeService.findEmployeeById(anyString(), anyList(), anyString())).thenReturn(Mono.just(employeeResponse));
        when(carService.updateCarWhenBookingIsFinished(anyString(), anyList(), any(CarUpdateDetails.class)))
                .thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(bookingService.closeBooking(apikeySecret, List.of("admin"), bookingClosingDetails))
                .expectError()
                .verify();
    }

    @Test
    void updateBookingTest_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);
        BookingRequest bookingRequest =
                TestUtils.getResourceAsJson("/data/BookingRequest.json", BookingRequest.class);
        BookingResponse bookingResponse =
                TestUtils.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);
        Outbox outbox = TestUtils.getResourceAsJson("/data/Outbox.json", Outbox.class);
        String apikeySecret = "token";

        when(bookingRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(booking));
        when(outboxService.saveBookingAndOutboxTransactional(any(Booking.class), any(Outbox.Operation.class)))
                .thenReturn(Mono.just(outbox));

        StepVerifier.create(bookingService.updateBooking(apikeySecret, List.of("admin"), "64f361caf291ae086e179547", bookingRequest))
                .expectNext(bookingResponse)
                .verifyComplete();
    }

    @Test
    void updateBookingTest_errorOnFindingBookingById() {
        BookingRequest bookingRequest =
                TestUtils.getResourceAsJson("/data/BookingRequest.json", BookingRequest.class);
        String apikeySecret = "token";

        when(bookingRepository.findById(any(ObjectId.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(bookingService.updateBooking(apikeySecret, List.of("admin"), "64f361caf291ae086e179547", bookingRequest))
                .expectError()
                .verify();
    }

    @Test
    void updateBookingTest_updatedCar_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);
        BookingRequest updatedBookingRequest =
                TestUtils.getResourceAsJson("/data/UpdatedBookingRequest.json", BookingRequest.class);
        BookingResponse updatedBookingResponse =
                TestUtils.getResourceAsJson("/data/UpdatedBookingResponse.json", BookingResponse.class);
        CarResponse carResponse =
                TestUtils.getResourceAsJson("/data/UpdatedNewCarResponse.json", CarResponse.class);
        Outbox outbox = TestUtils.getResourceAsJson("/data/Outbox.json", Outbox.class);
        outbox.getContent().setCarId(new ObjectId("64f361caf291ae086e179222"));
        String apikeySecret = "token";

        when(carService.findAvailableCarById(anyString(), anyList(), anyString())).thenReturn(Mono.just(carResponse));
        when(bookingRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(booking));
        when(outboxService.saveBookingAndOutboxTransactional(any(Booking.class), any(Outbox.Operation.class)))
                .thenReturn(Mono.just(outbox));
        when(carService.updateCarsStatus(anyString(), anyList(), anyList())).thenReturn(Flux.just(carResponse));

        StepVerifier.create(bookingService.updateBooking(apikeySecret, List.of("admin"), "64f361caf291ae086e179547", updatedBookingRequest))
                .expectNext(updatedBookingResponse)
                .verifyComplete();
    }

    @Test
    void getAmountSpentByUserTest_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);

        when(bookingRepository.findByCustomerUsername(anyString())).thenReturn(Flux.just(booking));

        StepVerifier.create(bookingService.getAmountSpentByLoggedInUser("username"))
                .expectNext(BigDecimal.valueOf(500))
                .verifyComplete();
    }

    @Test
    void getSumOfAllBookingAmountTest_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);

        when(bookingRepository.findAll()).thenReturn(Flux.just(booking));

        StepVerifier.create(bookingService.getSumOfAllBookingAmount())
                .expectNext(BigDecimal.valueOf(500))
                .verifyComplete();
    }

    @Test
    void findBookingByDateOfBookingTest_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);
        BookingResponse bookingResponse =
                TestUtils.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        when(reactiveMongoTemplate.find(any(Query.class), eq(Booking.class))).thenReturn(Flux.just(booking));

        StepVerifier.create(bookingService.findBookingsByDateOfBooking("2050-02-20"))
                .expectNext(bookingResponse)
                .verifyComplete();
    }

    @Test
    void findBookingByDateOfBookingTest_errorOnFindingByDateOfBooking() {
        when(reactiveMongoTemplate.find(any(Query.class), eq(Booking.class))).thenReturn(Flux.error(new Throwable()));

        StepVerifier.create(bookingService.findBookingsByDateOfBooking("2050-02-20"))
                .expectError()
                .verify();
    }

    @Test
    void deleteBookingByIdTest_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(bookingRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(booking));
        when(outboxService.processBookingDeletion(any(Booking.class), any(Outbox.Operation.class))).thenReturn(Mono.just(booking));
        when(carService.changeCarStatus(anyString(), anyList(), anyString(), any(CarState.class))).thenReturn(Mono.just(carResponse));

        StepVerifier.create(bookingService.deleteBookingById("apiKey", List.of("admin"), "64f361caf291ae086e179547"))
                .expectComplete()
                .verify();
    }

    @Test
    void deleteBookingByIdTest_errorOnFindingById() {
        when(bookingRepository.findById(any(ObjectId.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(bookingService.deleteBookingById("apiKey", List.of("admin"), "64f361caf291ae086e179547"))
                .expectError()
                .verify();
    }

}
