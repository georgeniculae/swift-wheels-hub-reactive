package com.swiftwheelshubreactive.customer.service;

import com.swiftwheelshubreactive.dto.RequestDetails;
import com.swiftwheelshubreactive.lib.util.WebClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private static final String SEPARATOR = "/";

    @Value("${booking-service.url}")
    private String url;

    private final WebClient webClient;

    public Mono<Void> deleteBookingsByUsername(RequestDetails requestDetails, String username) {
        return webClient.delete()
                .uri(url + SEPARATOR + username)
                .headers(WebClientUtil.setHttpHeaders(requestDetails.apikey(), requestDetails.roles()))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Void.class)
                .retryWhen(Retry.fixedDelay(5, Duration.ofSeconds(5)))
                .onErrorMap(e -> {
                    log.error("Error while sending request to: {}, error: {}", url, e.getMessage());

                    return e;
                });
    }

}
