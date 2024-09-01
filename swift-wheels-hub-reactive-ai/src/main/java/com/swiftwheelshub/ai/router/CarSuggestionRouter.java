package com.swiftwheelshub.ai.router;

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

    @Bean
    @SwaggerCarSuggestionRouterOperations
    public RouterFunction<ServerResponse> routeCarSuggestion(CarSuggestionHandler carSuggestionHandler) {
        return RouterFunctions.route(RequestPredicates.GET("/car-suggestion"), carSuggestionHandler::getChatOutput);
    }

}
