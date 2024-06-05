package com.swiftwheelshub.ai.controller;

import com.swiftwheelshub.ai.handler.CarSuggestionHandler;
import com.swiftwheelshub.ai.swaggerannotation.SwaggerCarSuggestionRouterOperations;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class CarSuggestionRouter {

    private final CarSuggestionHandler carSuggestionHandler;

    @Bean
    @SwaggerCarSuggestionRouterOperations
    public RouterFunction<ServerResponse> routeCarSuggestion(CarSuggestionHandler carSuggestionHandler) {
        return RouterFunctions.route(RequestPredicates.POST("/car-suggestion"), carSuggestionHandler::getChatOutput);

    }

}
