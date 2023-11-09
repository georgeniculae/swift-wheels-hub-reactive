package com.carrental.agency.router;

import com.carrental.agency.handler.EmployeeHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class EmployeeRouter {

    private static final String REQUEST_MAPPING = "/employees";

    @Bean
    public RouterFunction<ServerResponse> routeEmployee(EmployeeHandler employeeHandler) {
        return RouterFunctions.nest(RequestPredicates.path(REQUEST_MAPPING).and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                RouterFunctions.route(RequestPredicates.GET(""), employeeHandler::findAllEmployees)
                        .andRoute(RequestPredicates.GET("/branch/{id}"), employeeHandler::findEmployeesByBranchId)
                        .andRoute(RequestPredicates.GET("/filter/{filter}"), employeeHandler::findEmployeeByFilterInsensitiveCase)
                        .andRoute(RequestPredicates.GET("/count"), employeeHandler::countEmployees)
                        .andRoute(RequestPredicates.GET("/{id}"), employeeHandler::findEmployeeById)
                        .andRoute(RequestPredicates.POST(""), employeeHandler::saveEmployee)
                        .andRoute(RequestPredicates.PUT("/{id}"), employeeHandler::updateEmployee)
                        .andRoute(RequestPredicates.DELETE("/{id}"), employeeHandler::deleteEmployeeById));
    }

}
