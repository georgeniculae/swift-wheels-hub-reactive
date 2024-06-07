package com.swiftwheelshubreactive.booking.service;

import com.swiftwheelshubreactive.dto.EmployeeResponse;
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
public class EmployeeService {

    @Value("${webclient.url.swift-wheels-hub-agency-employees}")
    private String url;

    private static final String SEPARATOR = "/";

    private final WebClient webClient;

    public Mono<EmployeeResponse> findEmployeeById(String apiKey, List<String> roles, String employeeId) {
        return webClient.get()
                .uri(url + SEPARATOR + "{id}", employeeId)
                .headers(WebClientUtil.setHttpHeaders(apiKey, roles))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(EmployeeResponse.class)
                .retryWhen(Retry.fixedDelay(5, Duration.ofSeconds(5)))
                .onErrorMap(e -> {
                    log.error("Error while sending request to: {}, error: {}", url, e.getMessage());

                    return e;
                });
    }

}
