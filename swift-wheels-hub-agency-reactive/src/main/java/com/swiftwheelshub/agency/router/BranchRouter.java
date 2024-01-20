package com.swiftwheelshub.agency.router;

import com.swiftwheelshub.agency.handler.BranchHandler;
import com.swiftwheelshub.agency.swaggeroperation.SwaggerBranchRouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class BranchRouter {

    private static final String REQUEST_MAPPING = "/branches";

    @Bean
    @SwaggerBranchRouterOperations
    public RouterFunction<ServerResponse> routeBranch(BranchHandler branchHandler) {
        return RouterFunctions.nest(RequestPredicates.path(REQUEST_MAPPING).and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                RouterFunctions.route(RequestPredicates.GET(""), branchHandler::findAllBranches)
                        .andRoute(RequestPredicates.GET("/filter/{filter}"), branchHandler::findBranchByFilterInsensitiveCase)
                        .andRoute(RequestPredicates.GET("/count"), branchHandler::countBranches)
                        .andRoute(RequestPredicates.GET("/{id}"), branchHandler::findBranchById)
                        .andRoute(RequestPredicates.POST(""), branchHandler::saveBranch)
                        .andRoute(RequestPredicates.PUT("/{id}"), branchHandler::updateBranch)
                        .andRoute(RequestPredicates.DELETE("/{id}"), branchHandler::deleteBranchById));
    }

}
