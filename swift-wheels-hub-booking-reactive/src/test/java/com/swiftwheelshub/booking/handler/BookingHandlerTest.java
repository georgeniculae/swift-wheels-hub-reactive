package com.swiftwheelshub.booking.handler;

import com.swiftwheelshub.booking.service.BookingService;
import com.swiftwheelshub.booking.util.TestUtils;
import com.swiftwheelshub.dto.BookingClosingDetailsDto;
import com.swiftwheelshub.dto.BookingDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingHandlerTest {

    @InjectMocks
    private BookingHandler bookingHandler;

    @Mock
    private BookingService bookingService;

    @Test
    void findAllBookingsTest_success() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        MockServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(bookingService.findAllBookings()).thenReturn(Flux.just(bookingDto));

        StepVerifier.create(bookingHandler.findAllBookings(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void findAllBookingsTest_noResultReturned() {
        MockServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(bookingService.findAllBookings()).thenReturn(Flux.empty());

        StepVerifier.create(bookingHandler.findAllBookings(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void findBookingByIdTest_success() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        MockServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("id", "64f361caf291ae086e179547")
                .build();

        when(bookingService.findBookingById(anyString())).thenReturn(Mono.just(bookingDto));

        StepVerifier.create(bookingHandler.findBookingById(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void findBookingByIdTest_noResultReturned() {
        MockServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("id", "64f361caf291ae086e179547")
                .build();

        when(bookingService.findBookingById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(bookingHandler.findBookingById(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void findBookingsByDateOfBookingTest_success() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        MockServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("date", "2023-09-28")
                .build();

        when(bookingService.findBookingsByDateOfBooking(anyString())).thenReturn(Flux.just(bookingDto));

        StepVerifier.create(bookingHandler.findBookingsByDateOfBooking(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void findBookingsByDateOfBookingTest_noResultReturned() {
        MockServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("date", "2023-09-28")
                .build();

        when(bookingService.findBookingsByDateOfBooking(anyString())).thenReturn(Flux.empty());

        StepVerifier.create(bookingHandler.findBookingsByDateOfBooking(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void findBookingsByLoggedInUserTest_success() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        MockServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .header("X-USERNAME", "user")
                .build();

        when(bookingService.findBookingsByLoggedInUser(anyString())).thenReturn(Flux.just(bookingDto));

        StepVerifier.create(bookingHandler.findBookingsByLoggedInUser(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void findBookingsByLoggedInUserTest_noResultReturned() {
        MockServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .header("X-USERNAME", "user")
                .build();

        when(bookingService.findBookingsByLoggedInUser(anyString())).thenReturn(Flux.empty());

        StepVerifier.create(bookingHandler.findBookingsByLoggedInUser(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void getAmountSpentByLoggedInUserTest_success() {
        MockServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .header("X-USERNAME", "user")
                .build();

        when(bookingService.getAmountSpentByLoggedInUser(anyString())).thenReturn(Mono.just(500.0));

        StepVerifier.create(bookingHandler.getAmountSpentByLoggedInUser(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void getSumOfAllBookingAmountTest_success() {
        MockServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(bookingService.getSumOfAllBookingAmount()).thenReturn(Mono.just(500.0));

        StepVerifier.create(bookingHandler.getSumOfAllBookingAmount(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void countBookingsTest_success() {
        MockServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(bookingService.countBookings()).thenReturn(Mono.just(4L));

        StepVerifier.create(bookingHandler.countBookings(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void countBookingsOfLoggedInUserTest_success() {
        MockServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .header("X-USERNAME", "user")
                .build();

        when(bookingService.countBookingsOfLoggedInUser(anyString())).thenReturn(Mono.just(4L));

        StepVerifier.create(bookingHandler.countBookingsOfLoggedInUser(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void getCurrentDateTest_success() {
        MockServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(bookingService.getCurrentDate()).thenReturn(Mono.just(LocalDate.now()));

        StepVerifier.create(bookingHandler.getCurrentDate(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void saveBookingTest_success() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        MockServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.POST)
                .header("X-API-KEY", "apikey")
                .header("X-USERNAME", "user")
                .body(Mono.just(bookingDto));

        when(bookingService.saveBooking(anyString(), any(BookingDto.class))).thenReturn(Mono.just(bookingDto));

        StepVerifier.create(bookingHandler.saveBooking(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void closeBookingTest_success() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        BookingClosingDetailsDto bookingClosingDetailsDto =
                TestUtils.getResourceAsJson("/data/BookingClosingDetailsDto.json", BookingClosingDetailsDto.class);

        MockServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.POST)
                .header("X-API-KEY", "apikey")
                .header("X-USERNAME", "user")
                .body(Mono.just(bookingClosingDetailsDto));

        when(bookingService.closeBooking(anyString(), any(BookingClosingDetailsDto.class)))
                .thenReturn(Mono.just(bookingDto));

        StepVerifier.create(bookingHandler.closeBooking(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void updateBookingTest_success() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        MockServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.PUT)
                .header("X-API-KEY", "apikey")
                .header("X-USERNAME", "user")
                .pathVariable("id", "64f361caf291ae086e179547")
                .body(Mono.just(bookingDto));

        when(bookingService.updateBooking(anyString(), anyString(), any(BookingDto.class)))
                .thenReturn(Mono.just(bookingDto));

        StepVerifier.create(bookingHandler.updateBooking(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void deleteBookingByIdTest_success() {
        MockServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.DELETE)
                .header("X-API-KEY", "apiKey")
                .pathVariable("id", "64f361caf291ae086e179547")
                .build();

        when(bookingService.deleteBookingById(anyString(), anyString())).thenReturn(Mono.empty());

        StepVerifier.create(bookingHandler.deleteBookingById(serverRequest))
                .expectComplete()
                .verify();
    }

}
