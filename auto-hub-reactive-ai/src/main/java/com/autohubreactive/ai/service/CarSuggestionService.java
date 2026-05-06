package com.autohubreactive.ai.service;

import com.autohubreactive.ai.util.Constants;
import com.autohubreactive.dto.ai.CarSuggestionResponse;
import com.autohubreactive.dto.ai.TripInfo;
import com.autohubreactive.lib.exceptionhandling.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarSuggestionService {

    private static final int TOP_K = 10;

    private final ChatService chatService;
    private final CarVectorStoreService carVectorStoreService;

    public Mono<CarSuggestionResponse> getChatOutput(TripInfo tripInfo) {
        return carVectorStoreService.searchSimilarCars(buildQueryText(tripInfo), TOP_K)
                .flatMap(documents -> getCarSuggestionResponse(tripInfo, documents))
                .onErrorMap(e -> {
                    log.error("Error while getting chat response: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    private Mono<CarSuggestionResponse> getCarSuggestionResponse(TripInfo tripInfo, List<Document> documents) {
        List<String> cars = documents.stream()
                .map(Document::getText)
                .toList();

        return chatService.getChatReply(getText(), getParams(tripInfo, cars));
    }

    private String buildQueryText(TripInfo tripInfo) {
        return String.format(
                "Car rental for %d people starting from %s traveling to %s, Romania in %s for a %s trip",
                tripInfo.peopleCount(),
                tripInfo.startLocation(),
                tripInfo.destination(),
                getMonth(tripInfo.tripDate()),
                tripInfo.tripKind());
    }

    private String getText() {
        return """
                Which car from the following list {cars} is more suitable for rental from a rental car
                agency for a trip for {peopleCount} people starting from {startLocation} to {destination},
                Romania in {month}? The car will be used for {tripKind}.
                Please prefer cars available at rental offices close to {startLocation}.""";
    }

    private Map<String, Object> getParams(TripInfo tripInfo, List<String> cars) {
        return Map.of(
                Constants.CARS, cars,
                Constants.START_LOCATION, tripInfo.startLocation(),
                Constants.DESTINATION, tripInfo.destination(),
                Constants.PEOPLE_COUNT, tripInfo.peopleCount(),
                Constants.MONTH, getMonth(tripInfo.tripDate()),
                Constants.TRIP_KIND, tripInfo.tripKind()
        );
    }

    private String getMonth(LocalDate tripDate) {
        return tripDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

}
