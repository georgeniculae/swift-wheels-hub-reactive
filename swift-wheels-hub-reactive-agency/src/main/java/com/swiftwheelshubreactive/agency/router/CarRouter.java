package com.swiftwheelshubreactive.agency.router;

import com.swiftwheelshubreactive.agency.handler.CarHandler;
import com.swiftwheelshubreactive.agency.swaggeroperation.SwaggerCarRouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class CarRouter {

    private static final String REQUEST_MAPPING = "/cars";

    @Bean
    @SwaggerCarRouterOperations
    public RouterFunction<ServerResponse> routeCar(CarHandler carHandler) {
        return RouterFunctions.nest(RequestPredicates.path(REQUEST_MAPPING),
                RouterFunctions.route(RequestPredicates.GET(""), carHandler::findAllCars)
                        .andRoute(RequestPredicates.GET("/make/{make}"), carHandler::findCarsByMakeInsensitiveCase)
                        .andRoute(RequestPredicates.GET("/filter/{filter}"), carHandler::findCarsByFilterInsensitiveCase)
                        .andRoute(RequestPredicates.GET("/count"), carHandler::countCars)
                        .andRoute(RequestPredicates.GET("/{id}/availability"), carHandler::getAvailableCar)
                        .andRoute(RequestPredicates.GET("/available"), carHandler::getAllAvailableCars)
                        .andRoute(RequestPredicates.GET("/{id}/image"), carHandler::getCarImage)
                        .andRoute(RequestPredicates.GET("/{id}"), carHandler::findCarById)
                        .andRoute(RequestPredicates.POST(""), carHandler::saveCar)
                        .andRoute(RequestPredicates.POST("/upload"), carHandler::uploadCars)
                        .andRoute(RequestPredicates.PUT("/update-statuses"), carHandler::updateCarsStatuses)
                        .andRoute(RequestPredicates.PATCH("/{id}/change-status"), carHandler::updateCarStatus)
                        .andRoute(RequestPredicates.PUT("/{id}/update-after-return"), carHandler::updateCarWhenBookingIsClosed)
                        .andRoute(RequestPredicates.PUT("/{id}"), carHandler::updateCar)
                        .andRoute(RequestPredicates.DELETE("/{id}"), carHandler::deleteCarById));
    }

}
