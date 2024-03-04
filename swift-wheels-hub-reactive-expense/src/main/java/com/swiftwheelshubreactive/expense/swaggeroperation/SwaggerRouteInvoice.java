package com.swiftwheelshubreactive.expense.swaggeroperation;

import com.swiftwheelshubreactive.dto.EmployeeResponse;
import com.swiftwheelshubreactive.dto.InvoiceRequest;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import com.swiftwheelshubreactive.expense.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RouterOperations(
        {
                @RouterOperation(
                        method = RequestMethod.GET,
                        beanClass = InvoiceService.class,
                        beanMethod = "findAllInvoices",
                        operation = @Operation(
                                operationId = "findAllInvoices",
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
                        path = "/{id}",
                        beanClass = InvoiceService.class,
                        params = "id",
                        beanMethod = "findInvoiceById",
                        operation = @Operation(
                                operationId = "findInvoiceById",
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(schema = @Schema(implementation = EmployeeResponse.class))
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
                        path = "/active",
                        beanClass = InvoiceService.class,
                        beanMethod = "findAllActiveInvoices",
                        operation = @Operation(
                                operationId = "findAllActiveInvoices",
                                responses = {
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
                        path = "/by-comments",
                        beanClass = InvoiceService.class,
                        beanMethod = "findInvoiceByComments",
                        operation = @Operation(
                                operationId = "findInvoiceByComments",
                                responses = {
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
                        path = "/by-customer/{customerUsername}",
                        beanClass = InvoiceService.class,
                        params = "customerUsername",
                        beanMethod = "findAllInvoicesByCustomerUsername",
                        operation = @Operation(
                                operationId = "findAllInvoicesByCustomerUsername",
                                responses = {
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
                                },
                                parameters = @Parameter(in = ParameterIn.PATH, name = "customerUsername")
                        )
                ),
                @RouterOperation(
                        method = RequestMethod.GET,
                        path = "/count",
                        beanClass = InvoiceService.class,
                        beanMethod = "countInvoices",
                        operation = @Operation(
                                operationId = "countInvoices",
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(schema = @Schema(implementation = Long.class))
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
                        path = "/count-active",
                        beanClass = InvoiceService.class,
                        beanMethod = "countAllActiveInvoices",
                        operation = @Operation(
                                operationId = "countAllActiveInvoices",
                                responses = {
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
                        method = RequestMethod.PUT,
                        path = "/{id}",
                        beanClass = InvoiceService.class,
                        params = "id",
                        beanMethod = "closeInvoice",
                        operation = @Operation(
                                operationId = "closeInvoice",
                                requestBody = @RequestBody(
                                        description = "Close invoice",
                                        required = true,
                                        content = @Content(schema = @Schema(implementation = InvoiceRequest.class))
                                ),
                                responses = {
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
                                },
                                parameters = @Parameter(in = ParameterIn.PATH, name = "id")
                        )
                )
        }
)
public @interface SwaggerRouteInvoice {
}
