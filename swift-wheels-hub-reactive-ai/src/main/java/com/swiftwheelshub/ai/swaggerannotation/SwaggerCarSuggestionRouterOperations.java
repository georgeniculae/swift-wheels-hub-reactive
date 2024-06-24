package com.swiftwheelshub.ai.swaggerannotation;

import com.swiftwheelshub.ai.handler.CarSuggestionHandler;
import com.swiftwheelshubreactive.dto.CarSuggestionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDate;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@RouterOperations(
        {
                @RouterOperation(
                        method = RequestMethod.GET,
                        path = "/ai/car-suggestion",
                        beanClass = CarSuggestionHandler.class,
                        beanMethod = "getChatOutput",
                        operation = @Operation(
                                operationId = "getChatOutput",
                                parameters = {
                                        @Parameter(
                                                name = "destination",
                                                in = ParameterIn.QUERY,
                                                required = true,
                                                content = @Content(schema = @Schema(implementation = String.class))
                                        ),
                                        @Parameter(
                                                name = "peopleCount",
                                                in = ParameterIn.QUERY,
                                                required = true,
                                                content = @Content(schema = @Schema(implementation = Integer.class))
                                        ),
                                        @Parameter(
                                                name = "tripKind",
                                                in = ParameterIn.QUERY,
                                                required = true,
                                                content = @Content(schema = @Schema(implementation = String.class))
                                        ),
                                        @Parameter(
                                                name = "tripDate",
                                                in = ParameterIn.QUERY,
                                                required = true,
                                                content = @Content(schema = @Schema(implementation = LocalDate.class))
                                        )
                                },
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(schema = @Schema(implementation = CarSuggestionResponse.class))
                                        ),
                                        @ApiResponse(
                                                responseCode = "400",
                                                description = "Bad Request",
                                                content = @Content(schema = @Schema())
                                        ),
                                        @ApiResponse(
                                                responseCode = "500",
                                                description = "Internal Server Error",
                                                content = @Content(schema = @Schema())
                                        )
                                }
                        )
                )
        }
)
public @interface SwaggerCarSuggestionRouterOperations {
}
