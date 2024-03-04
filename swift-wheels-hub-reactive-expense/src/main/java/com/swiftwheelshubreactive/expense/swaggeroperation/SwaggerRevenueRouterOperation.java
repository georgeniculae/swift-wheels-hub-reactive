package com.swiftwheelshubreactive.expense.swaggeroperation;

import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import com.swiftwheelshubreactive.expense.service.InvoiceService;
import com.swiftwheelshubreactive.expense.service.RevenueService;
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
import java.math.BigDecimal;
import java.util.List;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RouterOperations(
        {
                @RouterOperation(
                        method = RequestMethod.GET,
                        beanClass = RevenueService.class,
                        beanMethod = "findAllRevenues",
                        operation = @Operation(
                                operationId = "findAllRevenues",
                                responses =
                                        {
                                                @ApiResponse(
                                                        responseCode = "200",
                                                        description = "Successful",
                                                        content = @Content(schema = @Schema(implementation = List.class))
                                                ),
                                                @ApiResponse(
                                                        responseCode = "400",
                                                        description = "Bad Request",
                                                        content = @Content(schema = @Schema(implementation = SwiftWheelsHubException.class))
                                                ),
                                                @ApiResponse(
                                                        responseCode = "500",
                                                        description = "Internal Server Error",
                                                        content = @Content(schema = @Schema(implementation = SwiftWheelsHubException.class))
                                                )
                                        }
                        )
                ),
                @RouterOperation(
                        method = RequestMethod.GET,
                        path = "/total",
                        beanClass = InvoiceService.class,
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
                                                content = @Content(schema = @Schema(implementation = SwiftWheelsHubException.class))
                                        ),
                                        @ApiResponse(
                                                responseCode = "500",
                                                description = "Internal Server Error",
                                                content = @Content(schema = @Schema(implementation = SwiftWheelsHubException.class))
                                        )
                                },
                                parameters = @Parameter(in = ParameterIn.PATH, name = "id")
                        )
                ),
                @RouterOperation(
                        method = RequestMethod.GET,
                        beanClass = RevenueService.class,
                        beanMethod = "findRevenuesByDate",
                        params = "date",
                        operation = @Operation(
                                operationId = "findRevenuesByDate",
                                responses =
                                        {
                                                @ApiResponse(
                                                        responseCode = "200",
                                                        description = "Successful",
                                                        content = @Content(schema = @Schema(implementation = List.class))
                                                ),
                                                @ApiResponse(
                                                        responseCode = "400",
                                                        description = "Bad Request",
                                                        content = @Content(schema = @Schema(implementation = SwiftWheelsHubException.class))
                                                ),
                                                @ApiResponse(
                                                        responseCode = "500",
                                                        description = "Internal Server Error",
                                                        content = @Content(schema = @Schema(implementation = SwiftWheelsHubException.class))
                                                )
                                        }, parameters = @Parameter(in = ParameterIn.PATH, name = "date")
                        )
                )
        }
)
public @interface SwaggerRevenueRouterOperation {
}
