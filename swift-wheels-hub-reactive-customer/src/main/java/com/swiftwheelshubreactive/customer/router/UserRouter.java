package com.swiftwheelshubreactive.customer.router;

import com.swiftwheelshubreactive.customer.handler.CustomerHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class UserRouter {

    @Bean
    public RouterFunction<ServerResponse> customerRoute(CustomerHandler customerHandler) {
        return RouterFunctions.nest(RequestPredicates.path("").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                RouterFunctions.route(RequestPredicates.GET("/current"), customerHandler::getCurrentUser)
                        .andRoute(RequestPredicates.GET("/count"), customerHandler::countUsers)
                        .andRoute(RequestPredicates.GET("/username/{username}"), customerHandler::findUserByUsername)
                        .andRoute(RequestPredicates.GET("/{id}"), customerHandler::findUserByUsername)
                        .andRoute(RequestPredicates.POST("/register"), customerHandler::registerUser)
                        .andRoute(RequestPredicates.PUT("/{id}"), customerHandler::updateUser)
                        .andRoute(RequestPredicates.DELETE("/{username}"), customerHandler::deleteUserById)
                        .andRoute(RequestPredicates.GET("/sign-out"), customerHandler::signOut));
    }

}
