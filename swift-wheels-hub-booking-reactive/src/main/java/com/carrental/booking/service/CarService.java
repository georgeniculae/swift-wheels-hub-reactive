package com.carrental.booking.service;

import com.carrental.dto.CarDetailsForUpdateDto;
import com.carrental.dto.CarDto;
import com.carrental.dto.CarStatusEnum;
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

    @Value("${webclient.url.car-rental-agency-cars}")
    private String url;

    private static final String SEPARATOR = "/";

    private static final String X_API_KEY = "X-API-KEY";

    private final WebClient webClient;

    public Mono<CarDto> findAvailableCarById(String apiKeyToken, String carId) {
        return webClient.get()
                .uri(url + SEPARATOR + "{id}" + SEPARATOR + "availability", carId)
                .header(X_API_KEY, apiKeyToken)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(CarDto.class)
                .onErrorMap(e -> {
                    log.error("Error while sending request to: {}, error: {}", url, e.getMessage());

                    return e;
                });
    }

    public Mono<CarDto> changeCarStatus(String apiKeyToken, String carId, CarStatusEnum carStatus) {
        return webClient.put()
                .uri(url + SEPARATOR + "{id}" + SEPARATOR + "change-car-status", carId)
                .header(X_API_KEY, apiKeyToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(carStatus)
                .retrieve()
                .bodyToMono(CarDto.class)
                .onErrorMap(e -> {
                    log.error("Error while sending request to: {}, error: {}", url, e.getMessage());

                    return e;
                });
    }

    public Mono<CarDto> updateCarWhenBookingIsFinished(String apiKeyToken, CarDetailsForUpdateDto carDetailsForUpdateDto) {
        return webClient.put()
                .uri(url + SEPARATOR + "{id}" + SEPARATOR + "update-after-closed-booking", carDetailsForUpdateDto.getCarId())
                .header(X_API_KEY, apiKeyToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(carDetailsForUpdateDto)
                .retrieve()
                .bodyToMono(CarDto.class)
                .onErrorMap(e -> {
                    log.error("Error while sending request to: {}, error: {}", url, e.getMessage());

                    return e;
                });
    }

    public Flux<CarDto> updateCarsStatus(String apiKeyToken,
                                         List<CarDetailsForUpdateDto> carDetailsForUpdateDtoList) {
        return webClient.put()
                .uri(url + SEPARATOR + SEPARATOR + "update-cars-status")
                .header(X_API_KEY, apiKeyToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(carDetailsForUpdateDtoList)
                .retrieve()
                .bodyToFlux(CarDto.class)
                .onErrorMap(e -> {
                    log.error("Error while sending request to: {}, error: {}", url, e.getMessage());

                    return e;
                });
    }

}
