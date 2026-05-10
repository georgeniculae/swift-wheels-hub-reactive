package com.autohubreactive.ai.handler;

import com.autohubreactive.ai.service.CarSuggestionService;
import com.autohubreactive.ai.util.Constants;
import com.autohubreactive.ai.validator.TripInfoValidator;
import com.autohubreactive.dto.ai.TripInfo;
import com.autohubreactive.lib.util.ServerRequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class CarSuggestionHandler {

    private final CarSuggestionService carSuggestionService;
    private final TripInfoValidator tripInfoValidator;

    @PreAuthorize("hasRole('user')")
    public Mono<ServerResponse> getChatOutput(ServerRequest serverRequest) {
        return getTripInfo(serverRequest)
                .flatMap(tripInfoValidator::validateBody)
                .flatMap(carSuggestionService::getChatOutput)
                .flatMap(carSuggestionResponse -> ServerResponse.ok().bodyValue(carSuggestionResponse));
    }

    private Mono<TripInfo> getTripInfo(ServerRequest serverRequest) {
        return Mono.just(
                TripInfo.builder()
                        .startLocation(ServerRequestUtil.getQueryParam(serverRequest, Constants.START_LOCATION))
                        .destination(ServerRequestUtil.getQueryParam(serverRequest, Constants.DESTINATION))
                        .peopleCount(Integer.parseInt(ServerRequestUtil.getQueryParam(serverRequest, Constants.PEOPLE_COUNT)))
                        .tripKind(ServerRequestUtil.getQueryParam(serverRequest, Constants.TRIP_KIND))
                        .tripDate(LocalDate.parse(ServerRequestUtil.getQueryParam(serverRequest, Constants.TRIP_DATE)))
                        .build()
        );
    }

}
