package com.swiftwheelshubreactive.agency.router;

import com.swiftwheelshubreactive.agency.handler.RentalOfficeHandler;
import com.swiftwheelshubreactive.agency.swaggeroperation.SwaggerRentalOfficeRouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RentalOfficeRouter {

    private static final String REQUEST_MAPPING = "/rental-offices";

    @Bean
    @SwaggerRentalOfficeRouterOperations
    public RouterFunction<ServerResponse> routeRentalOffice(RentalOfficeHandler rentalOfficeHandler) {
        return RouterFunctions.nest(RequestPredicates.path(REQUEST_MAPPING).and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                RouterFunctions.route(RequestPredicates.GET(""), rentalOfficeHandler::findAllRentalOffices)
                        .andRoute(RequestPredicates.GET("/filter/{filter}"), rentalOfficeHandler::findRentalOfficesByFilterInsensitiveCase)
                        .andRoute(RequestPredicates.GET("/count"), rentalOfficeHandler::countRentalOffices)
                        .andRoute(RequestPredicates.GET("/{id}"), rentalOfficeHandler::findRentalOfficeById)
                        .andRoute(RequestPredicates.PUT("/{id}"), rentalOfficeHandler::updateRentalOffice)
                        .andRoute(RequestPredicates.POST(""), rentalOfficeHandler::saveRentalOffice)
                        .andRoute(RequestPredicates.DELETE("/{id}"), rentalOfficeHandler::deleteRentalOfficeById));
    }

}
