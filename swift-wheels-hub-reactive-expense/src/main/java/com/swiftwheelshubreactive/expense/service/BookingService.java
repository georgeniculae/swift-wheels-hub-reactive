package com.swiftwheelshubreactive.expense.service;

import com.swiftwheelshubreactive.dto.BookingClosingDetails;
import com.swiftwheelshubreactive.dto.BookingResponse;
import com.swiftwheelshubreactive.dto.RequestDetails;
import com.swiftwheelshubreactive.lib.exceptionhandling.ExceptionUtil;
import com.swiftwheelshubreactive.lib.util.WebClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    @Value("${webClient.url.swift-wheels-hub-bookings}")
    private String url;

    private static final String SEPARATOR = "/";

    private final WebClient webClient;

    public Mono<Void> closeBooking(RequestDetails requestDetails, BookingClosingDetails bookingClosingDetails) {
        return webClient.post()
                .uri(url + SEPARATOR + "close-booking")
                .headers(WebClientUtil.setHttpHeaders(requestDetails.apikey(), requestDetails.roles()))
                .bodyValue(bookingClosingDetails)
                .retrieve()
                .bodyToMono(Void.class)
                .retryWhen(Retry.fixedDelay(5, Duration.ofSeconds(5)))
                .onErrorMap(this::handleException);
    }

    public Mono<BookingResponse> findBookingById(RequestDetails requestDetails, String bookingId) {
        return webClient.get()
                .uri(url + SEPARATOR + "{id}", bookingId)
                .headers(WebClientUtil.setHttpHeaders(requestDetails.apikey(), requestDetails.roles()))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(BookingResponse.class)
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(this::handleException);
    }

    private RuntimeException handleException(Throwable e) {
        log.error("Error while sending request to: {}, error: {}", url, e.getMessage());

        return ExceptionUtil.handleException(e);
    }

}
