package com.swiftwheelshub.booking.router;

import com.swiftwheelshub.booking.handler.BookingHandler;
import com.swiftwheelshub.booking.util.TestUtils;
import com.swiftwheelshub.dto.BookingDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = BookingRouter.class)
@WebFluxTest
class BookingRouterTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BookingHandler bookingHandler;

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void findAllBookingTest_success() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(List.of(bookingDto));

        when(bookingHandler.findAllBookings(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<BookingDto> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/list")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(BookingDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(bookingDto)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void findAllBookingTest_unauthorized() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(List.of(bookingDto));

        when(bookingHandler.findAllBookings(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/list")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void findBookingByIdTest_success() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(bookingDto);

        when(bookingHandler.findBookingById(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<BookingDto> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/{id}", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(BookingDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(bookingDto)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void findBookingByIdTest_unauthorized() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(bookingDto);

        when(bookingHandler.findBookingById(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/id/{id}", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void countBookingsTest_success() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(bookingDto);

        when(bookingHandler.countBookings(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<BookingDto> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/count")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(BookingDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(bookingDto)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void countBookingsTest_unauthorized() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(bookingDto);

        when(bookingHandler.countBookings(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/count")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void countByLoggedInUserTest_success() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(bookingDto);

        when(bookingHandler.countBookingsOfLoggedInUser(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<BookingDto> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/count-by-logged-in-user")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(BookingDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(bookingDto)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void countByLoggedInUserTest_unauthorized() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(bookingDto);

        when(bookingHandler.findBookingsByDateOfBooking(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/count-by-current-user")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void getCurrentDateTest_success() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(bookingDto);

        when(bookingHandler.getCurrentDate(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/current-date")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(LocalDate.class);
    }

    @Test
    @WithAnonymousUser
    void getCurrentDateTest_unauthorized() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(bookingDto);

        when(bookingHandler.getCurrentDate(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/current-date")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void saveBookingTest_success() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(bookingDto);

        when(bookingHandler.saveBooking(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<BookingDto> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri("/new")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(BookingDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(bookingDto)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void saveBookingTest_unauthorized() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(bookingDto);

        when(bookingHandler.saveBooking(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri("/new")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithAnonymousUser
    void saveBookingTest_forbidden() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(bookingDto);

        when(bookingHandler.saveBooking(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.post()
                .uri("/new")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void closeBookingTest_success() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(bookingDto);

        when(bookingHandler.closeBooking(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<BookingDto> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri("/close-booking")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(BookingDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(bookingDto)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void closeBookingTest_unauthorized() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(bookingDto);

        when(bookingHandler.closeBooking(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri("/close-booking")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithAnonymousUser
    void closeBookingTest_forbidden() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(bookingDto);

        when(bookingHandler.closeBooking(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.post()
                .uri("/close-booking")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void updateBookingTest_success() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(bookingDto);

        when(bookingHandler.updateBooking(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<BookingDto> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .put()
                .uri("/{id}", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(BookingDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(bookingDto)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void updateBookingTest_unauthorized() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(bookingDto);

        when(bookingHandler.updateBooking(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .put()
                .uri("/edit/{id}", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithAnonymousUser
    void updateBookingTest_forbidden() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(bookingDto);

        when(bookingHandler.updateBooking(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.put()
                .uri("/edit/{id}", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void deleteBookingByIdTest_success() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(bookingDto);

        when(bookingHandler.deleteBookingById(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<Void> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .delete()
                .uri("/{id}", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectComplete()
                .verify();
    }

    @Test
    @WithAnonymousUser
    void deleteBookingByIdTest_forbidden() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(bookingDto);

        when(bookingHandler.deleteBookingById(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .delete()
                .uri("/{id}", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

}
