package com.swiftwheelshubreactive.customer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private static final String SEPARATOR = "/";

    private static final String X_API_KEY = "X-API-KEY";

    private static final String X_ROLES = "X-ROLES";

    @Value("${booking-service.url}")
    private String url;

    private final WebClient webClient;

    public Mono<Void> deleteBookingsByUsername(String apiKey, List<String> roles, String username) {
        return webClient.delete()
                .uri(url + SEPARATOR + username)
                .header(X_API_KEY, apiKey)
                .header(X_ROLES, roles.toArray(String[]::new))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorMap(e -> {
                    log.error("Error while sending request to: {}, error: {}", url, e.getMessage());

                    return e;
                });
    }

}
