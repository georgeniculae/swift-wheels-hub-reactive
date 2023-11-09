package com.carrental.cloudgateway.router;

import com.carrental.cloudgateway.handler.AuthenticationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class AuthenticationRouter {

    @Bean
    public RouterFunction<ServerResponse> authenticateRoute(AuthenticationHandler authenticationHandler) {
        return RouterFunctions.route()
                .POST(
                        "/authenticate",
                        RequestPredicates.accept(MediaType.APPLICATION_JSON),
                        authenticationHandler::authenticateUser
                )
                .build();
    }

}
