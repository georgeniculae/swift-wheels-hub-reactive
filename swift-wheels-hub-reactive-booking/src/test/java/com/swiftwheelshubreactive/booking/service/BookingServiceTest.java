package com.swiftwheelshubreactive.booking.service;

import com.swiftwheelshubreactive.booking.mapper.BookingMapper;
import com.swiftwheelshubreactive.booking.mapper.BookingMapperImpl;
import com.swiftwheelshubreactive.booking.model.Outbox;
import com.swiftwheelshubreactive.booking.repository.BookingRepository;
import com.swiftwheelshubreactive.booking.util.TestUtil;
import com.swiftwheelshubreactive.dto.BookingClosingDetails;
import com.swiftwheelshubreactive.dto.BookingRequest;
import com.swiftwheelshubreactive.dto.BookingResponse;
import com.swiftwheelshubreactive.dto.CarResponse;
import com.swiftwheelshubreactive.dto.CarState;
import com.swiftwheelshubreactive.dto.CarUpdateDetails;
import com.swiftwheelshubreactive.dto.EmployeeResponse;
import com.swiftwheelshubreactive.dto.AuthenticationInfo;
import com.swiftwheelshubreactive.dto.UserInfo;
import com.swiftwheelshubreactive.model.Booking;
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

    @Mock
    private CustomerService customerService;

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
        BookingRequest bookingRequest =
                TestUtil.getResourceAsJson("/data/BookingRequest.json", BookingRequest.class);
        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);
        CarResponse carResponse =
                TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);
        UserInfo userInfo =
                TestUtil.getResourceAsJson("/data/UserInfo.json", UserInfo.class);
        Outbox outbox = TestUtil.getResourceAsJson("/data/Outbox.json", Outbox.class);
        String apikey = "apikey";
        AuthenticationInfo authenticationInfo = AuthenticationInfo.builder()
                .apikey(apikey)
                .roles(List.of("admin"))
                .build();

        when(carService.findAvailableCarById(any(AuthenticationInfo.class), anyString())).thenReturn(Mono.just(carResponse));
        when(customerService.findUserByUsername(any(AuthenticationInfo.class))).thenReturn(Mono.just(userInfo));
        when(outboxService.saveBookingAndOutbox(any(Booking.class), any(Outbox.Operation.class)))
                .thenReturn(Mono.just(outbox));
        when(carService.changeCarStatus(any(AuthenticationInfo.class), anyString(), any(CarState.class))).thenReturn(Mono.empty());

        StepVerifier.create(bookingService.saveBooking(authenticationInfo, bookingRequest))
                .expectNext(bookingResponse)
                .verifyComplete();

        verify(bookingMapper).mapEntityToDto(any(Booking.class));
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

        when(carService.findAvailableCarById(any(AuthenticationInfo.class), anyString())).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(bookingService.saveBooking(authenticationInfo, bookingRequest))
                .expectError()
                .verify();
    }

    @Test
    void closeBookingTest_success() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);
        Booking updatedClosedBooking =
                TestUtil.getResourceAsJson("/data/UpdatedClosedBooking.json", Booking.class);
        BookingResponse closedBookingResponse =
                TestUtil.getResourceAsJson("/data/ClosedBookingResponse.json", BookingResponse.class);
        BookingClosingDetails bookingClosingDetails =
                TestUtil.getResourceAsJson("/data/BookingClosingDetails.json", BookingClosingDetails.class);
        EmployeeResponse employeeResponse =
                TestUtil.getResourceAsJson("/data/EmployeeResponse.json", EmployeeResponse.class);
        String apikey = "apikey";
        AuthenticationInfo authenticationInfo = AuthenticationInfo.builder()
                .apikey(apikey)
                .roles(List.of("admin"))
                .build();

        when(bookingRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(booking));
        when(employeeService.findEmployeeById(any(AuthenticationInfo.class), anyString()))
                .thenReturn(Mono.just(employeeResponse));
        when(bookingRepository.save(any(Booking.class))).thenReturn(Mono.just(updatedClosedBooking));
        when(carService.updateCarWhenBookingIsFinished(any(AuthenticationInfo.class), any(CarUpdateDetails.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(bookingService.closeBooking(authenticationInfo, bookingClosingDetails))
                .expectNext(closedBookingResponse)
                .verifyComplete();
    }

    @Test
    void closeBookingTest_errorOnUpdatingCarWhenBookingIsFinished() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);
        BookingClosingDetails bookingClosingDetails =
                TestUtil.getResourceAsJson("/data/BookingClosingDetails.json", BookingClosingDetails.class);
        EmployeeResponse employeeResponse =
                TestUtil.getResourceAsJson("/data/EmployeeResponse.json", EmployeeResponse.class);
        String apikey = "apikey";
        AuthenticationInfo authenticationInfo = AuthenticationInfo.builder()
                .apikey(apikey)
                .roles(List.of("admin"))
                .build();

        when(bookingRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(Mono.just(booking));
        when(employeeService.findEmployeeById(any(AuthenticationInfo.class), anyString())).thenReturn(Mono.just(employeeResponse));
        when(carService.updateCarWhenBookingIsFinished(any(AuthenticationInfo.class), any(CarUpdateDetails.class)))
                .thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(bookingService.closeBooking(authenticationInfo, bookingClosingDetails))
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
        Outbox outbox = TestUtil.getResourceAsJson("/data/Outbox.json", Outbox.class);
        String apikey = "apikey";
        AuthenticationInfo authenticationInfo = AuthenticationInfo.builder()
                .apikey(apikey)
                .roles(List.of("admin"))
                .build();

        when(bookingRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(booking));
        when(outboxService.saveBookingAndOutbox(any(Booking.class), any(Outbox.Operation.class)))
                .thenReturn(Mono.just(outbox));

        StepVerifier.create(bookingService.updateBooking(authenticationInfo, "64f361caf291ae086e179547", bookingRequest))
                .expectNext(bookingResponse)
                .verifyComplete();
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

        StepVerifier.create(bookingService.updateBooking(authenticationInfo, "64f361caf291ae086e179547", bookingRequest))
                .expectError()
                .verify();
    }

    @Test
    void updateBookingTest_updatedCar_success() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);
        BookingRequest updatedBookingRequest =
                TestUtil.getResourceAsJson("/data/UpdatedBookingRequest.json", BookingRequest.class);
        BookingResponse updatedBookingResponse =
                TestUtil.getResourceAsJson("/data/UpdatedBookingResponse.json", BookingResponse.class);
        CarResponse carResponse =
                TestUtil.getResourceAsJson("/data/UpdatedNewCarResponse.json", CarResponse.class);
        Outbox outbox = TestUtil.getResourceAsJson("/data/Outbox.json", Outbox.class);
        outbox.getContent().setCarId(new ObjectId("64f361caf291ae086e179222"));
        String apikey = "apikey";
        AuthenticationInfo authenticationInfo = AuthenticationInfo.builder()
                .apikey(apikey)
                .roles(List.of("admin"))
                .build();

        when(carService.findAvailableCarById(any(AuthenticationInfo.class), anyString())).thenReturn(Mono.just(carResponse));
        when(bookingRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(booking));
        when(outboxService.saveBookingAndOutbox(any(Booking.class), any(Outbox.Operation.class)))
                .thenReturn(Mono.just(outbox));
        when(carService.updateCarsStatus(any(AuthenticationInfo.class), anyList())).thenReturn(Mono.empty());

        StepVerifier.create(bookingService.updateBooking(authenticationInfo, "64f361caf291ae086e179547", updatedBookingRequest))
                .expectNext(updatedBookingResponse)
                .verifyComplete();
    }

    @Test
    void getAmountSpentByUserTest_success() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);

        when(bookingRepository.findByCustomerUsername(anyString())).thenReturn(Flux.just(booking));

        StepVerifier.create(bookingService.getAmountSpentByLoggedInUser("username"))
                .expectNext(BigDecimal.valueOf(500))
                .verifyComplete();
    }

    @Test
    void getSumOfAllBookingAmountTest_success() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);

        when(bookingRepository.findAll()).thenReturn(Flux.just(booking));

        StepVerifier.create(bookingService.getSumOfAllBookingAmount())
                .expectNext(BigDecimal.valueOf(500))
                .verifyComplete();
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
        AuthenticationInfo authenticationInfo = AuthenticationInfo.builder()
                .apikey("apikey")
                .roles(List.of("admin"))
                .build();

        when(bookingRepository.findByCustomerUsername(anyString())).thenReturn(Flux.just(booking));
        when(outboxService.processBookingDeletion(anyList(), any(Outbox.Operation.class))).thenReturn(Mono.just(List.of("id")));
        when(carService.updateCarsStatus(any(AuthenticationInfo.class), anyList())).thenReturn(Mono.empty());

        StepVerifier.create(bookingService.deleteBookingByCustomerUsername(authenticationInfo, "user"))
                .expectComplete()
                .verify();
    }

    @Test
    void deleteBookingByCustomerUsernameTest_errorOnFindingById() {
        AuthenticationInfo authenticationInfo = AuthenticationInfo.builder()
                .apikey("apikey")
                .roles(List.of("admin"))
                .build();

        when(bookingRepository.findByCustomerUsername(anyString())).thenReturn(Flux.error(new Throwable()));

        StepVerifier.create(bookingService.deleteBookingByCustomerUsername(authenticationInfo, "user"))
                .expectError()
                .verify();
    }

}
