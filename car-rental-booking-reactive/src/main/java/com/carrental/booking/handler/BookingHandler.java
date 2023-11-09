package com.carrental.booking.handler;

import com.carrental.booking.service.BookingService;
import com.carrental.dto.BookingClosingDetailsDto;
import com.carrental.dto.BookingDto;
import com.carrental.lib.util.ServerRequestUtil;
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
                .flatMap(bookingDtoList -> ServerResponse.ok().bodyValue(bookingDtoList))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findBookingById(ServerRequest serverRequest) {
        return bookingService.findBookingById(ServerRequestUtil.getPathVariable(serverRequest, ID))
                .flatMap(bookingDto -> ServerResponse.ok().bodyValue(bookingDto))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findBookingsByDateOfBooking(ServerRequest serverRequest) {
        return bookingService.findBookingsByDateOfBooking(ServerRequestUtil.getPathVariable(serverRequest, DATE))
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(bookingDtoList -> ServerResponse.ok().bodyValue(bookingDtoList))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findBookingsByLoggedInUser(ServerRequest serverRequest) {
        return bookingService.findBookingsByLoggedInUser(ServerRequestUtil.getUsername(serverRequest))
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(bookingDtoList -> ServerResponse.ok().bodyValue(bookingDtoList))
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
        return serverRequest.bodyToMono(BookingDto.class)
                .flatMap(bookingDto ->
                        bookingService.saveBooking(ServerRequestUtil.getApiKeyHeader(serverRequest), bookingDto))
                .flatMap(bookingDto -> ServerResponse.ok().bodyValue(bookingDto));
    }

    public Mono<ServerResponse> closeBooking(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(BookingClosingDetailsDto.class)
                .flatMap(bookingClosingDetailsDto ->
                        bookingService.closeBooking(ServerRequestUtil.getApiKeyHeader(serverRequest), bookingClosingDetailsDto))
                .flatMap(bookingDto -> ServerResponse.ok().bodyValue(bookingDto));
    }

    public Mono<ServerResponse> updateBooking(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(BookingDto.class)
                .flatMap(bookingDto ->
                        bookingService.updateBooking(
                                ServerRequestUtil.getApiKeyHeader(serverRequest),
                                ServerRequestUtil.getPathVariable(serverRequest, ID),
                                bookingDto
                        )
                )
                .flatMap(bookingDto -> ServerResponse.ok().bodyValue(bookingDto));
    }

    public Mono<ServerResponse> deleteBookingById(ServerRequest serverRequest) {
        return bookingService.deleteBookingById(
                        ServerRequestUtil.getApiKeyHeader(serverRequest),
                        ServerRequestUtil.getPathVariable(serverRequest, ID)
                )
                .flatMap(bookingDto -> ServerResponse.noContent().build());
    }

}
