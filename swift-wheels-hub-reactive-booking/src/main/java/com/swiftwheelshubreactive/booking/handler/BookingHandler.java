package com.swiftwheelshubreactive.booking.handler;

import com.swiftwheelshubreactive.booking.service.BookingService;
import com.swiftwheelshubreactive.booking.validator.BookingClosingDetailsValidator;
import com.swiftwheelshubreactive.booking.validator.BookingRequestValidator;
import com.swiftwheelshubreactive.dto.BookingClosingDetails;
import com.swiftwheelshubreactive.dto.BookingRequest;
import com.swiftwheelshubreactive.dto.RequestDetails;
import com.swiftwheelshubreactive.lib.util.ServerRequestUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BookingHandler {

    private static final String ID = "id";
    private static final String DATE = "date";
    private static final String USERNAME = "username";
    private final BookingService bookingService;
    private final BookingRequestValidator bookingRequestValidator;
    private final BookingClosingDetailsValidator bookingClosingDetailsValidator;

    @PreAuthorize("hasRole('user')")
    public Mono<ServerResponse> findAllBookings(ServerRequest serverRequest) {
        return bookingService.findAllBookings()
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(bookingResponses -> ServerResponse.ok().bodyValue(bookingResponses))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @PreAuthorize("hasRole('user')")
    public Mono<ServerResponse> findBookingById(ServerRequest serverRequest) {
        return bookingService.findBookingById(serverRequest.pathVariable(ID))
                .flatMap(bookingResponse -> ServerResponse.ok().bodyValue(bookingResponse))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @PreAuthorize("hasRole('user')")
    public Mono<ServerResponse> findBookingsByDateOfBooking(ServerRequest serverRequest) {
        return bookingService.findBookingsByDateOfBooking(serverRequest.pathVariable(DATE))
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(bookingResponses -> ServerResponse.ok().bodyValue(bookingResponses))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @PreAuthorize("hasRole('user')")
    public Mono<ServerResponse> findBookingsByLoggedInUser(ServerRequest serverRequest) {
        return bookingService.findBookingsByLoggedInUser(ServerRequestUtil.getUsername(serverRequest))
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(bookingResponses -> ServerResponse.ok().bodyValue(bookingResponses))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @PreAuthorize("hasRole('user')")
    public Mono<ServerResponse> getAmountSpentByLoggedInUser(ServerRequest serverRequest) {
        return bookingService.getAmountSpentByLoggedInUser(ServerRequestUtil.getUsername(serverRequest))
                .flatMap(amount -> ServerResponse.ok().bodyValue(amount));
    }

    @PreAuthorize("hasRole('user')")
    public Mono<ServerResponse> getSumOfAllBookingAmount(ServerRequest serverRequest) {
        return bookingService.getSumOfAllBookingAmount()
                .flatMap(sum -> ServerResponse.ok().bodyValue(sum));
    }

    @PreAuthorize("hasRole('user')")
    public Mono<ServerResponse> countBookings(ServerRequest serverRequest) {
        return bookingService.countBookings()
                .flatMap(count -> ServerResponse.ok().bodyValue(count));
    }

    @PreAuthorize("hasRole('user')")
    public Mono<ServerResponse> countBookingsOfLoggedInUser(ServerRequest serverRequest) {
        return bookingService.countBookingsOfLoggedInUser(ServerRequestUtil.getUsername(serverRequest))
                .flatMap(count -> ServerResponse.ok().bodyValue(count));
    }

    @PreAuthorize("hasRole('user')")
    public Mono<ServerResponse> getCurrentDate(ServerRequest serverRequest) {
        return bookingService.getCurrentDate()
                .flatMap(currentDate -> ServerResponse.ok().bodyValue(currentDate));
    }

    @PreAuthorize("hasRole('user')")
    public Mono<ServerResponse> saveBooking(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(BookingRequest.class)
                .flatMap(bookingRequestValidator::validateBody)
                .flatMap(bookingRequest -> bookingService.saveBooking(
                        RequestDetails.builder()
                                .apikey(ServerRequestUtil.getApiKeyHeader(serverRequest))
                                .roles(ServerRequestUtil.getRolesHeader(serverRequest))
                                .build(),
                        bookingRequest)
                )
                .flatMap(bookingResponse -> ServerResponse.ok().bodyValue(bookingResponse));
    }

    @PreAuthorize("hasRole('user')")
    public Mono<ServerResponse> closeBooking(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(BookingClosingDetails.class)
                .flatMap(bookingClosingDetailsValidator::validateBody)
                .flatMap(bookingClosingDetails ->
                        bookingService.closeBooking(
                                RequestDetails.builder()
                                        .apikey(ServerRequestUtil.getApiKeyHeader(serverRequest))
                                        .roles(ServerRequestUtil.getRolesHeader(serverRequest))
                                        .build(),
                                bookingClosingDetails
                        )
                )
                .flatMap(bookingResponse -> ServerResponse.ok().bodyValue(bookingResponse));
    }

    @PreAuthorize("hasRole('user')")
    public Mono<ServerResponse> updateBooking(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(BookingRequest.class)
                .flatMap(bookingRequestValidator::validateBody)
                .flatMap(bookingRequest ->
                        bookingService.updateBooking(
                                RequestDetails.builder()
                                        .apikey(ServerRequestUtil.getApiKeyHeader(serverRequest))
                                        .roles(ServerRequestUtil.getRolesHeader(serverRequest))
                                        .build(),
                                serverRequest.pathVariable(ID),
                                bookingRequest
                        )
                )
                .flatMap(bookingResponse -> ServerResponse.ok().bodyValue(bookingResponse));
    }

    @PreAuthorize("hasRole('user')")
    public Mono<ServerResponse> deleteBookingByCustomerUsername(ServerRequest serverRequest) {
        return bookingService.deleteBookingByCustomerUsername(
                        RequestDetails.builder()
                                .apikey(ServerRequestUtil.getApiKeyHeader(serverRequest))
                                .roles(ServerRequestUtil.getRolesHeader(serverRequest))
                                .build(),
                        serverRequest.pathVariable(USERNAME)
                )
                .then(ServerResponse.noContent().build());
    }

}
