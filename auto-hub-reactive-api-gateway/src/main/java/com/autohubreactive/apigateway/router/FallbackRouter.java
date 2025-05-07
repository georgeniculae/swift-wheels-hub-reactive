package com.autohubreactive.apigateway.router;

import com.autohubreactive.apigateway.handler.FallbackHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class FallbackRouter {

    @Bean
    public RouterFunction<ServerResponse> routeFallback(FallbackHandler fallbackHandler) {
        return RouterFunctions.route(RequestPredicates.GET("/fallback"), fallbackHandler::fallback);
    }

}
