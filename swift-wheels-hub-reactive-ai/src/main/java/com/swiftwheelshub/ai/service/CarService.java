package com.swiftwheelshub.ai.service;

import com.swiftwheelshubreactive.dto.CarResponse;
import com.swiftwheelshubreactive.lib.exceptionhandling.ExceptionUtil;
import com.swiftwheelshubreactive.lib.util.WebClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarService {

    private static final String SEPARATOR = "/";
    private final WebClient webClient;

    @Value("${webclient.url.swift-wheels-hub-agency-cars}")
    private String url;

    public Flux<CarResponse> getAllAvailableCars(String apikey, List<String> roles) {
        return webClient.get()
                .uri(url + SEPARATOR + "available")
                .headers(WebClientUtil.setHttpHeaders(apikey, roles))
                .retrieve()
                .bodyToFlux(CarResponse.class)
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(e -> {
                    log.error("Error while sending request to: {}, error: {}", url, e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

}
