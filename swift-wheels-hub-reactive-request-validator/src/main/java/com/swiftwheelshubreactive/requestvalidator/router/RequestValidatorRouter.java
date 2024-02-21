package com.swiftwheelshubreactive.requestvalidator.router;

import com.swiftwheelshubreactive.requestvalidator.handler.RequestValidatorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RequestValidatorRouter {

    @Bean
    public RouterFunction<ServerResponse> routeRequest(RequestValidatorHandler requestValidatorHandler) {
        return RouterFunctions.route(RequestPredicates.POST("/validate"), requestValidatorHandler::validateRequest);
    }

    @Bean
    public RouterFunction<ServerResponse> repopulateRedisWithSwaggerFiles(RequestValidatorHandler requestValidatorHandler) {
        return RouterFunctions.route(RequestPredicates.POST("/invalidate/{microserviceName}"), requestValidatorHandler::repopulateRedisWithSwaggerFiles);
    }

}