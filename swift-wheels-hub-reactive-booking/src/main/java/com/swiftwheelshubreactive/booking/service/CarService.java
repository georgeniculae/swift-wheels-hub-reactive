package com.swiftwheelshubreactive.booking.service;

import com.swiftwheelshubreactive.dto.AuthenticationInfo;
import com.swiftwheelshubreactive.dto.CarResponse;
import com.swiftwheelshubreactive.dto.CarState;
import com.swiftwheelshubreactive.dto.CarUpdateDetails;
import com.swiftwheelshubreactive.dto.StatusUpdateResponse;
import com.swiftwheelshubreactive.dto.UpdateCarRequest;
import com.swiftwheelshubreactive.lib.exceptionhandling.ExceptionUtil;
import com.swiftwheelshubreactive.lib.util.WebClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarService {

    private static final String SEPARATOR = "/";
    private final WebClient webClient;

    @Value("${webclient.url.swift-wheels-hub-agency-cars}")
    private String url;

    public Mono<CarResponse> findAvailableCarById(AuthenticationInfo authenticationInfo, String carId) {
        return webClient.get()
                .uri(url + SEPARATOR + "{id}" + SEPARATOR + "availability", carId)
                .headers(WebClientUtil.setHttpHeaders(authenticationInfo.apikey(), authenticationInfo.roles()))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(CarResponse.class)
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(this::handleException);
    }

    public Mono<StatusUpdateResponse> changeCarStatus(AuthenticationInfo authenticationInfo,
                                                      String carId,
                                                      CarState carState,
                                                      int retries) {
        return webClient.patch()
                .uri(url + SEPARATOR + "{id}" + SEPARATOR + "change-status?carState={carState}", carId, carState)
                .headers(WebClientUtil.setHttpHeaders(authenticationInfo.apikey(), authenticationInfo.roles()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(this::getStatusUpdateResponseMono)
                .retryWhen(Retry.fixedDelay(retries, Duration.ofSeconds(5)))
                .onErrorResume(_ -> Mono.just(getCarUpdateResponse()));
    }

    public Mono<StatusUpdateResponse> updateCarsStatuses(AuthenticationInfo authenticationInfo,
                                                         List<UpdateCarRequest> updateCarRequests,
                                                         int retries) {
        return webClient.put()
                .uri(url + SEPARATOR + "update-statuses")
                .headers(WebClientUtil.setHttpHeaders(authenticationInfo.apikey(), authenticationInfo.roles()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(updateCarRequests)
                .exchangeToFlux(this::processClientResponseFlux)
                .collectList()
                .flatMap(this::checkCarsUpdateResponse)
                .retryWhen(Retry.fixedDelay(retries, Duration.ofSeconds(5)))
                .onErrorResume(_ -> Mono.just(getCarUpdateResponse()));
    }

    public Mono<StatusUpdateResponse> updateCarWhenBookingIsFinished(AuthenticationInfo authenticationInfo,
                                                                     CarUpdateDetails carUpdateDetails,
                                                                     int retries) {
        return webClient.put()
                .uri(url + SEPARATOR + "{id}" + SEPARATOR + "update-after-return", carUpdateDetails.carId())
                .headers(WebClientUtil.setHttpHeaders(authenticationInfo.apikey(), authenticationInfo.roles()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(carUpdateDetails)
                .exchangeToMono(this::getStatusUpdateResponseMono)
                .retryWhen(Retry.fixedDelay(retries, Duration.ofSeconds(5)))
                .onErrorResume(_ -> Mono.just(getCarUpdateResponse()));
    }

    private Mono<StatusUpdateResponse> getStatusUpdateResponseMono(ClientResponse clientResponse) {
        if (clientResponse.statusCode().isError()) {
            return Mono.just(getCarUpdateResponse());
        }

        return clientResponse.bodyToMono(StatusUpdateResponse.class);
    }

    private Flux<StatusUpdateResponse> processClientResponseFlux(ClientResponse clientResponse) {
        if (clientResponse.statusCode().isError()) {
            return Flux.just(getCarUpdateResponse());
        }

        return clientResponse.bodyToFlux(StatusUpdateResponse.class);
    }

    private StatusUpdateResponse getCarUpdateResponse() {
        return StatusUpdateResponse.builder()
                .isUpdateSuccessful(false)
                .build();
    }

    private boolean checkIfBothCarsWereSuccessfullyUpdated(List<StatusUpdateResponse> statusUpdateResponses) {
        return statusUpdateResponses.stream()
                .allMatch(StatusUpdateResponse::isUpdateSuccessful);
    }

    private Mono<StatusUpdateResponse> checkCarsUpdateResponse(List<StatusUpdateResponse> statusUpdateResponses) {
        boolean successful = checkIfBothCarsWereSuccessfullyUpdated(statusUpdateResponses);

        StatusUpdateResponse statusUpdateResponse = StatusUpdateResponse.builder()
                .isUpdateSuccessful(successful)
                .build();

        return Mono.just(statusUpdateResponse);
    }

    private RuntimeException handleException(Throwable e) {
        log.error("Error while sending request to: {}, error: {}", url, e.getMessage());

        return ExceptionUtil.handleException(e);
    }

}
