package com.carrental.booking.service;

import com.carrental.booking.mapper.BookingMapper;
import com.carrental.booking.mapper.BookingMapperImpl;
import com.carrental.booking.model.Outbox;
import com.carrental.booking.repository.BookingRepository;
import com.carrental.booking.util.TestUtils;
import com.swiftwheelshub.model.Booking;
import com.carrental.dto.BookingClosingDetailsDto;
import com.carrental.dto.BookingDto;
import com.carrental.dto.CarDetailsForUpdateDto;
import com.carrental.dto.CarDto;
import com.carrental.dto.CarStatusEnum;
import com.carrental.dto.EmployeeDto;
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

import java.time.LocalDate;

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
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        when(bookingRepository.findAll()).thenReturn(Flux.just(booking));

        StepVerifier.create(bookingService.findAllBookings())
                .expectNext(bookingDto)
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
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        when(bookingRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(booking));

        StepVerifier.create(bookingService.findBookingById("64f361caf291ae086e179547"))
                .expectNext(bookingDto)
                .verifyComplete();
    }

    @Test
    void findBookingsByLoggedInUserTest_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        when(bookingRepository.findByCustomerUsername(anyString())).thenReturn(Flux.just(booking));

        StepVerifier.create(bookingService.findBookingsByLoggedInUser("admin"))
                .expectNext(bookingDto)
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
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);
        Outbox outbox = TestUtils.getResourceAsJson("/data/Outbox.json", Outbox.class);
        String token = "token";

        when(carService.findAvailableCarById(anyString(), anyString())).thenReturn(Mono.just(carDto));
        when(outboxService.saveBookingAndOutboxTransactional(any(Booking.class), any(Outbox.Operation.class)))
                .thenReturn(Mono.just(outbox));
        when(carService.changeCarStatus(anyString(), anyString(), any(CarStatusEnum.class))).thenReturn(Mono.just(carDto));

        StepVerifier.create(bookingService.saveBooking(token, bookingDto))
                .expectNext(bookingDto)
                .verifyComplete();

        verify(bookingMapper, times(1)).mapEntityToDto(any(Booking.class));
    }

    @Test
    void saveBookingTest_errorOnFindingAvailableCarById() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);
        String token = "token";

        when(carService.findAvailableCarById(anyString(), anyString())).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(bookingService.saveBooking(token, bookingDto))
                .expectError()
                .verify();
    }

    @Test
    void closeBookingTest_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);
        BookingDto closedBookingDto =
                TestUtils.getResourceAsJson("/data/ClosedBookingDto.json", BookingDto.class);
        BookingClosingDetailsDto bookingClosingDetailsDto =
                TestUtils.getResourceAsJson("/data/BookingClosingDetailsDto.json", BookingClosingDetailsDto.class);
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);
        EmployeeDto employeeDto = TestUtils.getResourceAsJson("/data/EmployeeDto.json", EmployeeDto.class);
        String token = "token";

        when(bookingRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(Mono.just(booking));
        when(employeeService.findEmployeeById(anyString(), anyString())).thenReturn(Mono.just(employeeDto));
        when(carService.updateCarWhenBookingIsFinished(anyString(), any(CarDetailsForUpdateDto.class)))
                .thenReturn(Mono.just(carDto));

        StepVerifier.create(bookingService.closeBooking(token, bookingClosingDetailsDto))
                .expectNext(closedBookingDto)
                .verifyComplete();
    }

    @Test
    void closeBookingTest_errorOnUpdatingCarWhenBookingIsFinished() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);
        BookingClosingDetailsDto bookingClosingDetailsDto =
                TestUtils.getResourceAsJson("/data/BookingClosingDetailsDto.json", BookingClosingDetailsDto.class);
        EmployeeDto employeeDto = TestUtils.getResourceAsJson("/data/EmployeeDto.json", EmployeeDto.class);
        String token = "token";

        when(bookingRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(Mono.just(booking));
        when(employeeService.findEmployeeById(anyString(), anyString())).thenReturn(Mono.just(employeeDto));
        when(carService.updateCarWhenBookingIsFinished(anyString(), any(CarDetailsForUpdateDto.class)))
                .thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(bookingService.closeBooking(token, bookingClosingDetailsDto))
                .expectError()
                .verify();
    }

    @Test
    void updateBookingTest_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);
        Outbox outbox = TestUtils.getResourceAsJson("/data/Outbox.json", Outbox.class);
        String token = "token";

        when(bookingRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(booking));
        when(outboxService.saveBookingAndOutboxTransactional(any(Booking.class), any(Outbox.Operation.class)))
                .thenReturn(Mono.just(outbox));

        StepVerifier.create(bookingService.updateBooking(token, "64f361caf291ae086e179547", bookingDto))
                .expectNext(bookingDto)
                .verifyComplete();
    }

    @Test
    void updateBookingTest_errorOnFindingBookingById() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);
        String token = "token";

        when(bookingRepository.findById(any(ObjectId.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(bookingService.updateBooking(token, "64f361caf291ae086e179547", bookingDto))
                .expectError()
                .verify();
    }

    @Test
    void updateBookingTest_updatedCar_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);
        BookingDto updatedBookingDto = TestUtils.getResourceAsJson("/data/UpdatedBookingDto.json", BookingDto.class);
        CarDto carDto = TestUtils.getResourceAsJson("/data/UpdatedNewCarDto.json", CarDto.class);
        Outbox outbox = TestUtils.getResourceAsJson("/data/Outbox.json", Outbox.class);
        outbox.getContent().setCarId(new ObjectId("64f361caf291ae086e179222"));
        String token = "token";

        when(carService.findAvailableCarById(anyString(), anyString())).thenReturn(Mono.just(carDto));
        when(bookingRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(booking));
        when(outboxService.saveBookingAndOutboxTransactional(any(Booking.class), any(Outbox.Operation.class)))
                .thenReturn(Mono.just(outbox));
        when(carService.updateCarsStatus(anyString(), anyList())).thenReturn(Flux.just(carDto));

        StepVerifier.create(bookingService.updateBooking(token, "64f361caf291ae086e179547", updatedBookingDto))
                .expectNext(updatedBookingDto)
                .verifyComplete();
    }

    @Test
    void getAmountSpentByUserTest_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);

        when(bookingRepository.findByCustomerUsername(anyString())).thenReturn(Flux.just(booking));

        StepVerifier.create(bookingService.getAmountSpentByLoggedInUser("username"))
                .expectNext(500.0)
                .verifyComplete();
    }

    @Test
    void getSumOfAllBookingAmountTest_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);

        when(bookingRepository.findAll()).thenReturn(Flux.just(booking));

        StepVerifier.create(bookingService.getSumOfAllBookingAmount())
                .expectNext(500.0)
                .verifyComplete();
    }

    @Test
    void findBookingByDateOfBookingTest_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        when(reactiveMongoTemplate.find(any(Query.class), eq(Booking.class))).thenReturn(Flux.just(booking));

        StepVerifier.create(bookingService.findBookingsByDateOfBooking("2050-02-20"))
                .expectNext(bookingDto)
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
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        when(bookingRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(booking));
        when(outboxService.processBookingDeletion(any(Booking.class), any(Outbox.Operation.class))).thenReturn(Mono.just(booking));
        when(carService.changeCarStatus(anyString(), anyString(), any(CarStatusEnum.class))).thenReturn(Mono.just(carDto));

        StepVerifier.create(bookingService.deleteBookingById("apiKey", "64f361caf291ae086e179547"))
                .expectComplete()
                .verify();
    }

    @Test
    void deleteBookingByIdTest_errorOnFindingById() {
        when(bookingRepository.findById(any(ObjectId.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(bookingService.deleteBookingById("apiKey", "64f361caf291ae086e179547"))
                .expectError()
                .verify();
    }

}
