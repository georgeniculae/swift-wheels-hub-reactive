package com.autohubreactive.booking.handler;

import com.autohubreactive.booking.service.bookingprocessing.BookingService;
import com.autohubreactive.booking.util.TestUtil;
import com.autohubreactive.booking.validator.BookingRequestValidator;
import com.autohubreactive.dto.AuthenticationInfo;
import com.autohubreactive.dto.BookingRequest;
import com.autohubreactive.dto.BookingResponse;
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

    @Mock
    private BookingRequestValidator bookingRequestValidator;

    @Test
    void findAllBookingsTest_success() {
        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        MockServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(bookingService.findAllBookings()).thenReturn(Flux.just(bookingResponse));

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
        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        MockServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("id", "64f361caf291ae086e179547")
                .build();

        when(bookingService.findBookingById(anyString())).thenReturn(Mono.just(bookingResponse));

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
        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        MockServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("date", "2023-09-28")
                .build();

        when(bookingService.findBookingsByDateOfBooking(anyString())).thenReturn(Flux.just(bookingResponse));

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
        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        MockServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .header("X-USERNAME", "user")
                .build();

        when(bookingService.findBookingsByLoggedInUser(anyString())).thenReturn(Flux.just(bookingResponse));

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
        BookingRequest bookingRequest =
                TestUtil.getResourceAsJson("/data/BookingRequest.json", BookingRequest.class);

        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        MockServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.POST)
                .header("X-API-KEY", "apikey")
                .header("X-USERNAME", "user")
                .body(Mono.just(bookingRequest));

        when(bookingRequestValidator.validateBody(any())).thenReturn(Mono.just(bookingRequest));
        when(bookingService.saveBooking(any(AuthenticationInfo.class), any(BookingRequest.class)))
                .thenReturn(Mono.just(bookingResponse));

        StepVerifier.create(bookingHandler.saveBooking(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void updateBookingTest_success() {
        BookingRequest bookingRequest =
                TestUtil.getResourceAsJson("/data/BookingRequest.json", BookingRequest.class);

        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        MockServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.PUT)
                .header("X-API-KEY", "apikey")
                .header("X-USERNAME", "user")
                .pathVariable("id", "64f361caf291ae086e179547")
                .body(Mono.just(bookingRequest));

        when(bookingRequestValidator.validateBody(any())).thenReturn(Mono.just(bookingRequest));
        when(bookingService.updateBooking(any(AuthenticationInfo.class), anyString(), any(BookingRequest.class)))
                .thenReturn(Mono.just(bookingResponse));

        StepVerifier.create(bookingHandler.updateBooking(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

}
