package com.swiftwheelshub.ai.handler;

import com.swiftwheelshub.ai.service.CarSuggestionService;
import com.swiftwheelshub.ai.validator.TripInfoValidator;
import com.swiftwheelshubreactive.dto.TripInfo;
import com.swiftwheelshubreactive.lib.util.ServerRequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CarSuggestionHandler {

    private final CarSuggestionService carSuggestionService;
    private final TripInfoValidator tripInfoValidator;

    public Mono<ServerResponse> getChatOutput(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(TripInfo.class)
                .flatMap(tripInfoValidator::validateBody)
                .flatMapMany(tripInfo -> carSuggestionService.getChatOutput(
                                ServerRequestUtil.getApiKeyHeader(serverRequest),
                                ServerRequestUtil.getRolesHeader(serverRequest),
                                tripInfo
                        )
                )
                .collectList()
                .flatMap(output -> ServerResponse.ok().bodyValue(output));
    }

}
