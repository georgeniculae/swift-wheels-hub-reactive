package com.swiftwheelshub.booking.handler;

import com.swiftwheelshub.booking.service.BookingService;
import com.swiftwheelshub.dto.BookingClosingDetails;
import com.swiftwheelshub.dto.BookingRequest;
import com.swiftwheelshub.lib.util.ServerRequestUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
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

    public Mono<ServerResponse> findAllBookings(ServerRequest serverRequest) {
        return bookingService.findAllBookings()
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(bookingResponses -> ServerResponse.ok().bodyValue(bookingResponses))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findBookingById(ServerRequest serverRequest) {
        return bookingService.findBookingById(ServerRequestUtil.getPathVariable(serverRequest, ID))
                .flatMap(bookingResponse -> ServerResponse.ok().bodyValue(bookingResponse))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findBookingsByDateOfBooking(ServerRequest serverRequest) {
        return bookingService.findBookingsByDateOfBooking(ServerRequestUtil.getPathVariable(serverRequest, DATE))
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(bookingResponses -> ServerResponse.ok().bodyValue(bookingResponses))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findBookingsByLoggedInUser(ServerRequest serverRequest) {
        return bookingService.findBookingsByLoggedInUser(ServerRequestUtil.getUsername(serverRequest))
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(bookingResponses -> ServerResponse.ok().bodyValue(bookingResponses))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getAmountSpentByLoggedInUser(ServerRequest serverRequest) {
        return bookingService.getAmountSpentByLoggedInUser(ServerRequestUtil.getUsername(serverRequest))
                .flatMap(amount -> ServerResponse.ok().bodyValue(amount));
    }

    public Mono<ServerResponse> getSumOfAllBookingAmount(ServerRequest serverRequest) {
        return bookingService.getSumOfAllBookingAmount()
                .flatMap(sum -> ServerResponse.ok().bodyValue(sum));
    }

    public Mono<ServerResponse> countBookings(ServerRequest serverRequest) {
        return bookingService.countBookings()
                .flatMap(count -> ServerResponse.ok().bodyValue(count));
    }

    public Mono<ServerResponse> countBookingsOfLoggedInUser(ServerRequest serverRequest) {
        return bookingService.countBookingsOfLoggedInUser(ServerRequestUtil.getUsername(serverRequest))
                .flatMap(count -> ServerResponse.ok().bodyValue(count));
    }

    public Mono<ServerResponse> getCurrentDate(ServerRequest serverRequest) {
        return bookingService.getCurrentDate()
                .flatMap(currentDate -> ServerResponse.ok().bodyValue(currentDate));
    }

    public Mono<ServerResponse> saveBooking(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(BookingRequest.class)
                .flatMap(bookingRequest ->
                        bookingService.saveBooking(ServerRequestUtil.getApiKeyHeader(serverRequest), bookingRequest))
                .flatMap(bookingResponse -> ServerResponse.ok().bodyValue(bookingResponse));
    }

    public Mono<ServerResponse> closeBooking(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(BookingClosingDetails.class)
                .flatMap(bookingClosingDetails ->
                        bookingService.closeBooking(ServerRequestUtil.getApiKeyHeader(serverRequest), bookingClosingDetails))
                .flatMap(bookingResponse -> ServerResponse.ok().bodyValue(bookingResponse));
    }

    public Mono<ServerResponse> updateBooking(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(BookingRequest.class)
                .flatMap(bookingRequest ->
                        bookingService.updateBooking(
                                ServerRequestUtil.getApiKeyHeader(serverRequest),
                                ServerRequestUtil.getPathVariable(serverRequest, ID),
                                bookingRequest
                        )
                )
                .flatMap(bookingResponse -> ServerResponse.ok().bodyValue(bookingResponse));
    }

    public Mono<ServerResponse> deleteBookingById(ServerRequest serverRequest) {
        return bookingService.deleteBookingById(
                        ServerRequestUtil.getApiKeyHeader(serverRequest),
                        ServerRequestUtil.getPathVariable(serverRequest, ID)
                )
                .flatMap(bookingResponse -> ServerResponse.noContent().build());
    }

}
