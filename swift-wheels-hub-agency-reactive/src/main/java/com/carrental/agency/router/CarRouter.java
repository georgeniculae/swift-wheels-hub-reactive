package com.carrental.agency.router;

import com.carrental.agency.handler.CarHandler;
import com.carrental.agency.swaggeroperation.SwaggerCarRouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
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
        return RouterFunctions.nest(RequestPredicates.path(REQUEST_MAPPING).and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                RouterFunctions.route(RequestPredicates.GET(""), carHandler::findAllCars)
                        .andRoute(RequestPredicates.GET("/make/{make}"), carHandler::findCarsByMake)
                        .andRoute(RequestPredicates.GET("/filter/{filter}"), carHandler::findCarsByFilterInsensitiveCase)
                        .andRoute(RequestPredicates.GET("/count"), carHandler::countCars)
                        .andRoute(RequestPredicates.GET("/{id}/availability"), carHandler::getAvailableCar)
                        .andRoute(RequestPredicates.GET("/{id}"), carHandler::findCarById)
                        .andRoute(RequestPredicates.POST(""), carHandler::saveCar)
                        .andRoute(RequestPredicates.POST("/upload"), carHandler::uploadCars)
                        .andRoute(RequestPredicates.PUT("/update-cars-status"), carHandler::updateCarsStatus)
                        .andRoute(RequestPredicates.PUT("/{id}/change-car-status"), carHandler::updateCarStatus)
                        .andRoute(RequestPredicates.PUT("/{id}/update-after-closed-booking"), carHandler::updateCarWhenBookingIsClosed)
                        .andRoute(RequestPredicates.PUT("/{id}"), carHandler::updateCar)
                        .andRoute(RequestPredicates.DELETE("/{id}"), carHandler::deleteCarById));
    }

}
