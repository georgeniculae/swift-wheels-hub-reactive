package com.swiftwheelshubreactive.expense.service;

import com.swiftwheelshubreactive.dto.AuthenticationInfo;
import com.swiftwheelshubreactive.dto.CarUpdateDetails;
import com.swiftwheelshubreactive.dto.StatusUpdateResponse;
import com.swiftwheelshubreactive.lib.util.WebClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarService {

    private static final String SEPARATOR = "/";
    private final WebClient webClient;

    @Value("${webClient.url.swift-wheels-hub-agency-cars}")
    private String url;

    public Mono<StatusUpdateResponse> setCarAsAvailable(AuthenticationInfo authenticationInfo,
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
                .onErrorResume(e -> {
                    log.error("Error while setting car as available: {}", e.getMessage());

                    return Mono.just(getCarUpdateResponse());
                });
    }

    private Mono<StatusUpdateResponse> getStatusUpdateResponseMono(ClientResponse clientResponse) {
        if (clientResponse.statusCode().isError()) {
            return Mono.just(getCarUpdateResponse());
        }

        return clientResponse.bodyToMono(StatusUpdateResponse.class);
    }

    private StatusUpdateResponse getCarUpdateResponse() {
        return StatusUpdateResponse.builder()
                .isUpdateSuccessful(false)
                .build();
    }

}
