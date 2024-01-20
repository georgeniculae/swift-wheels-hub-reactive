package com.swiftwheelshub.expense.service;

import com.carrental.dto.BookingClosingDetailsDto;
import com.carrental.dto.BookingDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    @Value("${webClient.url.swift-wheels-hub-bookings}")
    private String url;

    private static final String SEPARATOR = "/";

    private static final String X_API_KEY = "X-API-KEY";

    private final WebClient webClient;

    public Mono<BookingDto> closeBooking(String apiKeyToken, BookingClosingDetailsDto bookingClosingDetailsDto) {
        return webClient.post()
                .uri(url + SEPARATOR + "close-booking")
                .header(X_API_KEY, apiKeyToken)
                .bodyValue(bookingClosingDetailsDto)
                .retrieve()
                .bodyToMono(BookingDto.class)
                .onErrorMap(e -> {
                    log.error("Error while sending request to: {}, error: {}", url, e.getMessage());

                    return e;
                });
    }

    public Mono<BookingDto> findBookingById(String apiKeyToken, String bookingId) {
        return webClient.get()
                .uri(url + SEPARATOR + "/{id}", bookingId)
                .header(X_API_KEY, apiKeyToken)
                .retrieve()
                .bodyToMono(BookingDto.class)
                .onErrorMap(e -> {
                    log.error("Error while sending request to: {}, error: {}", url, e.getMessage());

                    return e;
                });
    }

}
