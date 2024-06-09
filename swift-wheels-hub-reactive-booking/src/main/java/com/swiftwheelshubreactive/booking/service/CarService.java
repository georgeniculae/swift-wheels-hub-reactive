package com.swiftwheelshubreactive.booking.service;

import com.swiftwheelshubreactive.dto.CarResponse;
import com.swiftwheelshubreactive.dto.CarState;
import com.swiftwheelshubreactive.dto.CarUpdateDetails;
import com.swiftwheelshubreactive.dto.UpdateCarRequest;
import com.swiftwheelshubreactive.lib.exceptionhandling.ExceptionUtil;
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
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarService {

    @Value("${webclient.url.swift-wheels-hub-agency-cars}")
    private String url;

    private static final String SEPARATOR = "/";

    private final WebClient webClient;

    public Mono<CarResponse> findAvailableCarById(String apiKey, List<String> roles, String carId) {
        return webClient.get()
                .uri(url + SEPARATOR + "{id}" + SEPARATOR + "availability", carId)
                .headers(WebClientUtil.setHttpHeaders(apiKey, roles))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(CarResponse.class)
                .onErrorMap(this::getSwiftWheelsHubException);
    }

    public Mono<Void> changeCarStatus(String apiKey, List<String> roles, String carId, CarState carState) {
        return webClient.put()
                .uri(url + SEPARATOR + "{id}" + SEPARATOR + "change-status", carId)
                .headers(WebClientUtil.setHttpHeaders(apiKey, roles))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(carState)
                .retrieve()
                .bodyToMono(Void.class)
                .retryWhen(Retry.fixedDelay(5, Duration.ofSeconds(5)))
                .onErrorMap(this::getSwiftWheelsHubException);
    }

    public Mono<Void> updateCarWhenBookingIsFinished(String apiKey, List<String> roles,
                                                     CarUpdateDetails carUpdateDetails) {
        return webClient.put()
                .uri(url + SEPARATOR + "{id}" + SEPARATOR + "update-after-return", carUpdateDetails.carId())
                .headers(WebClientUtil.setHttpHeaders(apiKey, roles))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(carUpdateDetails)
                .retrieve()
                .bodyToMono(Void.class)
                .retryWhen(Retry.fixedDelay(5, Duration.ofSeconds(5)))
                .onErrorMap(this::getSwiftWheelsHubException);
    }

    public Mono<Void> updateCarsStatus(String apiKey, List<String> roles,
                                       List<UpdateCarRequest> updateCarRequests) {
        return webClient.put()
                .uri(url + SEPARATOR + "update-statuses")
                .headers(WebClientUtil.setHttpHeaders(apiKey, roles))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(updateCarRequests)
                .retrieve()
                .bodyToFlux(CarResponse.class)
                .retryWhen(Retry.fixedDelay(5, Duration.ofSeconds(5)))
                .then()
                .onErrorMap(this::getSwiftWheelsHubException);
    }

    private RuntimeException getSwiftWheelsHubException(Throwable e) {
        log.error("Error while sending request to: {}, error: {}", url, e.getMessage());

        return ExceptionUtil.getException(e);
    }

}
