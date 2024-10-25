package com.swiftwheelshubreactive.expense.service;

import com.swiftwheelshubreactive.dto.AuthenticationInfo;
import com.swiftwheelshubreactive.dto.BookingClosingDetails;
import com.swiftwheelshubreactive.dto.BookingResponse;
import com.swiftwheelshubreactive.dto.BookingUpdateResponse;
import com.swiftwheelshubreactive.lib.exceptionhandling.ExceptionUtil;
import com.swiftwheelshubreactive.lib.util.WebClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private static final String SEPARATOR = "/";
    private final WebClient webClient;

    @Value("${webClient.url.swift-wheels-hub-bookings}")
    private String url;

    public Mono<BookingUpdateResponse> closeBooking(AuthenticationInfo authenticationInfo,
                                                    BookingClosingDetails bookingClosingDetails,
                                                    int retries) {
        return webClient.post()
                .uri(url + SEPARATOR + "close-booking")
                .headers(WebClientUtil.setHttpHeaders(authenticationInfo.apikey(), authenticationInfo.roles()))
                .bodyValue(bookingClosingDetails)
                .exchangeToMono(this::handleBookingResponse)
                .subscribeOn(Schedulers.boundedElastic())
                .retryWhen(Retry.fixedDelay(retries, Duration.ofSeconds(5)))
                .onErrorResume(e -> {
                    log.error("Error while trying to close booking: {}", e.getMessage());

                    return Mono.just(getFailedBookingUpdateResponse());
                });
    }

    public Mono<BookingResponse> findBookingById(AuthenticationInfo authenticationInfo, String bookingId) {
        return webClient.get()
                .uri(url + SEPARATOR + "{id}", bookingId)
                .headers(WebClientUtil.setHttpHeaders(authenticationInfo.apikey(), authenticationInfo.roles()))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(BookingResponse.class)
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(this::handleException);
    }

    private Mono<BookingUpdateResponse> handleBookingResponse(ClientResponse clientResponse) {
        if (clientResponse.statusCode().isError()) {
            return Mono.just(getFailedBookingUpdateResponse());
        }

        return clientResponse.bodyToMono(BookingUpdateResponse.class);
    }

    private BookingUpdateResponse getFailedBookingUpdateResponse() {
        return BookingUpdateResponse.builder()
                .isSuccessful(false)
                .build();
    }

    private RuntimeException handleException(Throwable e) {
        log.error("Error while sending request to: {}, error: {}", url, e.getMessage());

        return ExceptionUtil.handleException(e);
    }

}
