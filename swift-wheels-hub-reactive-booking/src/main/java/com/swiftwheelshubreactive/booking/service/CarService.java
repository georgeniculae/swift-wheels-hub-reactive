package com.swiftwheelshubreactive.booking.service;

import com.swiftwheelshubreactive.dto.CarResponse;
import com.swiftwheelshubreactive.dto.CarState;
import com.swiftwheelshubreactive.dto.CarUpdateDetails;
import com.swiftwheelshubreactive.dto.UpdateCarRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarService {

    @Value("${webclient.url.swift-wheels-hub-agency-cars}")
    private String url;

    private static final String SEPARATOR = "/";

    private static final String X_API_KEY = "X-API-KEY";

    private static final String X_ROLES = "X-ROLES";

    private final WebClient webClient;

    public Mono<CarResponse> findAvailableCarById(String apiKey, List<String> roles, String carId) {
        return webClient.get()
                .uri(url + SEPARATOR + "{id}" + SEPARATOR + "availability", carId)
                .header(X_API_KEY, apiKey)
                .header(X_ROLES, roles.toArray(String[]::new))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(CarResponse.class)
                .onErrorMap(e -> {
                    log.error("Error while sending request to: {}, error: {}", url, e.getMessage());

                    return e;
                });
    }

    public Mono<CarResponse> changeCarStatus(String apiKey, List<String> roles, String carId, CarState carState) {
        return webClient.put()
                .uri(url + SEPARATOR + "{id}" + SEPARATOR + "change-status", carId)
                .header(X_API_KEY, apiKey)
                .header(X_ROLES, roles.toArray(String[]::new))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(carState)
                .retrieve()
                .bodyToMono(CarResponse.class)
                .onErrorMap(e -> {
                    log.error("Error while sending request to: {}, error: {}", url, e.getMessage());

                    return e;
                });
    }

    public Mono<CarResponse> updateCarWhenBookingIsFinished(String apiKey, List<String> roles,
                                                            CarUpdateDetails carUpdateDetails) {
        return webClient.put()
                .uri(url + SEPARATOR + "{id}" + SEPARATOR + "update-after-return", carUpdateDetails.carId())
                .header(X_API_KEY, apiKey)
                .header(X_ROLES, roles.toArray(String[]::new))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(carUpdateDetails)
                .retrieve()
                .bodyToMono(CarResponse.class)
                .onErrorMap(e -> {
                    log.error("Error while sending request to: {}, error: {}", url, e.getMessage());

                    return e;
                });
    }

    public Flux<CarResponse> updateCarsStatus(String apiKey, List<String> roles,
                                              List<UpdateCarRequest> updateCarRequests) {
        return webClient.put()
                .uri(url + SEPARATOR + SEPARATOR + "update-statuses")
                .header(X_API_KEY, apiKey)
                .header(X_ROLES, roles.toArray(String[]::new))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(updateCarRequests)
                .retrieve()
                .bodyToFlux(CarResponse.class)
                .onErrorMap(e -> {
                    log.error("Error while sending request to: {}, error: {}", url, e.getMessage());

                    return e;
                });
    }

}