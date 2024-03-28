package com.swiftwheelshubreactive.customer.router;

import com.swiftwheelshubreactive.customer.handler.CustomerHandler;
import com.swiftwheelshubreactive.customer.swaggeroperation.SwaggerCustomerRouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class CustomerRouter {

    @Bean
    @SwaggerCustomerRouterOperations
    public RouterFunction<ServerResponse> customerRoute(CustomerHandler customerHandler) {
        return RouterFunctions.nest(RequestPredicates.path("").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                RouterFunctions.route(RequestPredicates.GET("/current"), customerHandler::getCurrentUser)
                        .andRoute(RequestPredicates.GET("/count"), customerHandler::countUsers)
                        .andRoute(RequestPredicates.GET("/username/{username}"), customerHandler::findUserByUsername)
                        .andRoute(RequestPredicates.POST("/register"), customerHandler::registerUser)
                        .andRoute(RequestPredicates.PUT("/{id}"), customerHandler::updateUser)
                        .andRoute(RequestPredicates.DELETE("/current"), customerHandler::deleteCurrentUser)
                        .andRoute(RequestPredicates.DELETE("/{username}"), customerHandler::deleteUserByUsername)
                        .andRoute(RequestPredicates.GET("/sign-out"), customerHandler::signOut));
    }

}
