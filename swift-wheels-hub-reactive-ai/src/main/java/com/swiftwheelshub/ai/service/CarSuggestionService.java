package com.swiftwheelshub.ai.service;

import com.swiftwheelshubreactive.dto.CarResponse;
import com.swiftwheelshubreactive.dto.TripInfo;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarSuggestionService {

    private final AiAssistant aiAssistant;
    private final CarService carService;

    public Mono<String> getChatOutput(String apikey, List<String> roles, TripInfo tripInfo) {
        return getAvailableCars(apikey, roles)
                .collectList()
                .map(cars -> createChatPrompt(tripInfo, cars))
                .flatMap(this::getGeneratedOutput)
                .onErrorMap(e -> {
                    log.error("Error while getting chat response: {}", e.getMessage());

                    return new SwiftWheelsHubException(e.getMessage());
                });
    }

    private Mono<String> getGeneratedOutput(String prompt) {
        return Mono.fromCallable(() -> aiAssistant.chat(prompt))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Flux<String> getAvailableCars(String apikey, List<String> roles) {
        return carService.getAllAvailableCars(apikey, roles)
                .map(this::getCarDetails);
    }

    private String getCarDetails(CarResponse carResponse) {
        return carResponse.make() + " " + carResponse.model() + " from " + carResponse.yearOfProduction();
    }

    private String createChatPrompt(TripInfo tripInfo, List<String> cars) {
        return String.format(
                """
                        Which car from the following list %s is more suitable for rental from a car rental agency for
                        a trip for %s people to %s, Romania in %s? The car will be used for %s.""",
                cars,
                tripInfo.peopleCount(),
                tripInfo.destination(),
                getMonth(tripInfo.tripDate()),
                tripInfo.tripKind()
        );
    }

    private String getMonth(LocalDate tripDate) {
        return tripDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

}
