package com.swiftwheelshub.ai.handler;

import com.swiftwheelshub.ai.service.CarSuggestionService;
import com.swiftwheelshub.ai.validator.TripInfoValidator;
import com.swiftwheelshubreactive.dto.TripInfo;
import com.swiftwheelshubreactive.lib.util.ServerRequestUtil;
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

    private static final String DESTINATION = "destination";
    private static final String PEOPLE_COUNT = "peopleCount";
    private static final String TRIP_KIND = "tripKind";
    private static final String TRIP_DATE = "tripDate";
    private final CarSuggestionService carSuggestionService;
    private final TripInfoValidator tripInfoValidator;

    @PreAuthorize("hasRole('user')")
    public Mono<ServerResponse> getChatOutput(ServerRequest serverRequest) {
        return getTripInfo(serverRequest)
                .flatMap(tripInfoValidator::validateBody)
                .flatMap(tripInfo -> carSuggestionService.getChatOutput(
                                ServerRequestUtil.getApiKeyHeader(serverRequest),
                                ServerRequestUtil.getRolesHeader(serverRequest),
                                tripInfo
                        )
                )
                .flatMap(carSuggestionResponse -> ServerResponse.ok().bodyValue(carSuggestionResponse));
    }

    private Mono<TripInfo> getTripInfo(ServerRequest serverRequest) {
        return Mono.just(
                TripInfo.builder()
                        .destination(ServerRequestUtil.getQueryParam(serverRequest, DESTINATION))
                        .peopleCount(Integer.parseInt(ServerRequestUtil.getQueryParam(serverRequest, PEOPLE_COUNT)))
                        .tripKind(ServerRequestUtil.getQueryParam(serverRequest, TRIP_KIND))
                        .tripDate(LocalDate.parse(ServerRequestUtil.getQueryParam(serverRequest, TRIP_DATE)))
                        .build()
        );
    }

}
