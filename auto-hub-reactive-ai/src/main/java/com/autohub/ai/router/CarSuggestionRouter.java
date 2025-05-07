package com.autohub.ai.router;

import com.autohub.ai.handler.CarSuggestionHandler;
import com.autohub.ai.swaggerannotation.SwaggerCarSuggestionRouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class CarSuggestionRouter {

    @Bean
    @SwaggerCarSuggestionRouterOperations
    public RouterFunction<ServerResponse> routeCarSuggestion(CarSuggestionHandler carSuggestionHandler) {
        return RouterFunctions.route(RequestPredicates.GET("/car-suggestion"), carSuggestionHandler::getChatOutput);
    }

}
