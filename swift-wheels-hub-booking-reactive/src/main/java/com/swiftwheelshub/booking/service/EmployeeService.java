package com.swiftwheelshub.booking.service;

import com.carrental.dto.EmployeeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    @Value("${webclient.url.car-rental-agency-employees}")
    private String url;

    private static final String SEPARATOR = "/";

    private static final String X_API_KEY = "X-API-KEY";

    private final WebClient webClient;

    public Mono<EmployeeDto> findEmployeeById(String apiKeyToken, String employeeId) {
        return webClient.get()
                .uri(url + SEPARATOR + "{id}", employeeId)
                .header(X_API_KEY, apiKeyToken)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(EmployeeDto.class)
                .onErrorMap(e -> {
                    log.error("Error while sending request to: {}, error: {}", url, e.getMessage());

                    return e;
                });
    }

}
