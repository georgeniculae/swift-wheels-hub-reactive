package com.swiftwheelshubreactive.booking.handler;

import com.swiftwheelshubreactive.booking.service.BookingService;
import com.swiftwheelshubreactive.dto.BookingClosingDetails;
import com.swiftwheelshubreactive.dto.BookingRequest;
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
    private final BookingService bookingService;

    @PreAuthorize("hasAuthority('user')")
    public Mono<ServerResponse> findAllBookings(ServerRequest serverRequest) {
        return bookingService.findAllBookings()
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(bookingResponses -> ServerResponse.ok().bodyValue(bookingResponses))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @PreAuthorize("hasAuthority('user')")
    public Mono<ServerResponse> findBookingById(ServerRequest serverRequest) {
        return bookingService.findBookingById(ServerRequestUtil.getPathVariable(serverRequest, ID))
                .flatMap(bookingResponse -> ServerResponse.ok().bodyValue(bookingResponse))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @PreAuthorize("hasAuthority('user')")
    public Mono<ServerResponse> findBookingsByDateOfBooking(ServerRequest serverRequest) {
        return bookingService.findBookingsByDateOfBooking(ServerRequestUtil.getPathVariable(serverRequest, DATE))
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(bookingResponses -> ServerResponse.ok().bodyValue(bookingResponses))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @PreAuthorize("hasAuthority('user')")
    public Mono<ServerResponse> findBookingsByLoggedInUser(ServerRequest serverRequest) {
        return bookingService.findBookingsByLoggedInUser(ServerRequestUtil.getUsername(serverRequest))
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(bookingResponses -> ServerResponse.ok().bodyValue(bookingResponses))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @PreAuthorize("hasAuthority('user')")
    public Mono<ServerResponse> getAmountSpentByLoggedInUser(ServerRequest serverRequest) {
        return bookingService.getAmountSpentByLoggedInUser(ServerRequestUtil.getUsername(serverRequest))
                .flatMap(amount -> ServerResponse.ok().bodyValue(amount));
    }

    @PreAuthorize("hasAuthority('user')")
    public Mono<ServerResponse> getSumOfAllBookingAmount(ServerRequest serverRequest) {
        return bookingService.getSumOfAllBookingAmount()
                .flatMap(sum -> ServerResponse.ok().bodyValue(sum));
    }

    @PreAuthorize("hasAuthority('user')")
    public Mono<ServerResponse> countBookings(ServerRequest serverRequest) {
        return bookingService.countBookings()
                .flatMap(count -> ServerResponse.ok().bodyValue(count));
    }

    @PreAuthorize("hasAuthority('user')")
    public Mono<ServerResponse> countBookingsOfLoggedInUser(ServerRequest serverRequest) {
        return bookingService.countBookingsOfLoggedInUser(ServerRequestUtil.getUsername(serverRequest))
                .flatMap(count -> ServerResponse.ok().bodyValue(count));
    }

    @PreAuthorize("hasAuthority('user')")
    public Mono<ServerResponse> getCurrentDate(ServerRequest serverRequest) {
        return bookingService.getCurrentDate()
                .flatMap(currentDate -> ServerResponse.ok().bodyValue(currentDate));
    }

    @PreAuthorize("hasAuthority('user')")
    public Mono<ServerResponse> saveBooking(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(BookingRequest.class)
                .flatMap(bookingRequest -> bookingService.saveBooking(
                        ServerRequestUtil.getApiKeyHeader(serverRequest),
                        ServerRequestUtil.getRolesHeader(serverRequest),
                        bookingRequest)
                )
                .flatMap(bookingResponse -> ServerResponse.ok().bodyValue(bookingResponse));
    }

    @PreAuthorize("hasAuthority('user')")
    public Mono<ServerResponse> closeBooking(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(BookingClosingDetails.class)
                .flatMap(bookingClosingDetails ->
                        bookingService.closeBooking(
                                ServerRequestUtil.getApiKeyHeader(serverRequest),
                                ServerRequestUtil.getRolesHeader(serverRequest),
                                bookingClosingDetails
                        )
                )
                .flatMap(bookingResponse -> ServerResponse.ok().bodyValue(bookingResponse));
    }

    @PreAuthorize("hasAuthority('user')")
    public Mono<ServerResponse> updateBooking(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(BookingRequest.class)
                .flatMap(bookingRequest ->
                        bookingService.updateBooking(
                                ServerRequestUtil.getApiKeyHeader(serverRequest),
                                ServerRequestUtil.getRolesHeader(serverRequest),
                                ServerRequestUtil.getPathVariable(serverRequest, ID),
                                bookingRequest
                        )
                )
                .flatMap(bookingResponse -> ServerResponse.ok().bodyValue(bookingResponse));
    }

    @PreAuthorize("hasAuthority('user')")
    public Mono<ServerResponse> deleteBookingById(ServerRequest serverRequest) {
        return bookingService.deleteBookingById(
                        ServerRequestUtil.getApiKeyHeader(serverRequest),
                        ServerRequestUtil.getRolesHeader(serverRequest),
                        ServerRequestUtil.getPathVariable(serverRequest, ID)
                )
                .flatMap(bookingResponse -> ServerResponse.noContent().build());
    }

}
