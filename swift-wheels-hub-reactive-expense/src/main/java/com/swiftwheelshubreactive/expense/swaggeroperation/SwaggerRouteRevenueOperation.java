package com.swiftwheelshubreactive.expense.swaggeroperation;

import com.swiftwheelshubreactive.dto.RevenueResponse;
import com.swiftwheelshubreactive.expense.handler.RevenueHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RouterOperations(
        {
                @RouterOperation(
                        method = RequestMethod.GET,
                        path = "/revenues",
                        beanClass = RevenueHandler.class,
                        beanMethod = "findAllRevenues",
                        operation = @Operation(
                                operationId = "findAllRevenues",
                                responses =
                                        {
                                                @ApiResponse(
                                                        responseCode = "200",
                                                        description = "Successful",
                                                        content = @Content(
                                                                array = @ArraySchema(schema = @Schema(implementation = RevenueResponse.class)),
                                                                mediaType = MediaType.APPLICATION_JSON_VALUE
                                                        )
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
                ),
                @RouterOperation(
                        method = RequestMethod.GET,
                        path = "/revenues/total",
                        beanClass = RevenueHandler.class,
                        beanMethod = "getTotalAmount",
                        operation = @Operation(
                                operationId = "getTotalAmount",
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(schema = @Schema(implementation = BigDecimal.class))
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
                ),
                @RouterOperation(
                        method = RequestMethod.GET,
                        path = "/revenues/{date}",
                        beanClass = RevenueHandler.class,
                        beanMethod = "findRevenuesByDate",
                        operation = @Operation(
                                operationId = "findRevenuesByDate",
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(
                                                        array = @ArraySchema(schema = @Schema(implementation = RevenueResponse.class)),
                                                        mediaType = MediaType.APPLICATION_JSON_VALUE
                                                )
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
                                },
                                parameters = @Parameter(
                                        name = "date",
                                        in = ParameterIn.PATH,
                                        required = true,
                                        content = @Content(schema = @Schema(implementation = String.class))
                                )
                        )
                )
        }
)
public @interface SwaggerRouteRevenueOperation {
}
