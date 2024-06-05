package com.swiftwheelshub.ai.service;

import com.swiftwheelshubreactive.dto.CarResponse;
import com.swiftwheelshubreactive.exception.ExceptionUtil;
import com.swiftwheelshubreactive.lib.util.WebClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarService {

    private static final String SEPARATOR = "/";

    @Value("${web-client.url.swift-wheels-hub-agency-cars}")
    private String url;

    private final WebClient webClient;

    public Flux<CarResponse> getAllAvailableCars(String apikey, List<String> roles) {
        return webClient.get()
                .uri(url + SEPARATOR + "available")
                .headers(WebClientUtil.mutateHttpHeaders(apikey, roles))
                .retrieve()
                .bodyToFlux(CarResponse.class)
                .onErrorMap(this::getSwiftWheelsHubException);
    }

    private RuntimeException getSwiftWheelsHubException(Throwable e) {
        log.error("Error while sending request to: {}, error: {}", url, e.getMessage());

        return ExceptionUtil.getException(e);
    }

}
