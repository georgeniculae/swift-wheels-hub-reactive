package com.swiftwheelshubreactive.expense.service;

import com.swiftwheelshubreactive.dto.BookingClosingDetails;
import com.swiftwheelshubreactive.dto.BookingResponse;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    @Value("${webClient.url.swift-wheels-hub-bookings}")
    private String url;

    private static final String SEPARATOR = "/";

    private static final String X_API_KEY = "X-API-KEY";

    private static final String X_ROLES = "X-ROLES";

    private final WebClient webClient;

    public Mono<Void> closeBooking(String apiKey, List<String> roles, BookingClosingDetails bookingClosingDetails) {
        return webClient.post()
                .uri(url + SEPARATOR + "close-booking")
                .header(X_API_KEY, apiKey)
                .header(X_ROLES, roles.toArray(String[]::new))
                .bodyValue(bookingClosingDetails)
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorMap(this::getSwiftWheelsHubException);
    }

    public Mono<BookingResponse> findBookingById(String apiKey, List<String> roles, String bookingId) {
        return webClient.get()
                .uri(url + SEPARATOR + "/{id}", bookingId)
                .header(X_API_KEY, apiKey)
                .header(X_ROLES, roles.toArray(String[]::new))
                .retrieve()
                .bodyToMono(BookingResponse.class)
                .onErrorMap(this::getSwiftWheelsHubException);
    }

    private SwiftWheelsHubException getSwiftWheelsHubException(Throwable e) {
        log.error("Error while sending request to: {}, error: {}", url, e.getMessage());

        return new SwiftWheelsHubException(e);
    }

}
