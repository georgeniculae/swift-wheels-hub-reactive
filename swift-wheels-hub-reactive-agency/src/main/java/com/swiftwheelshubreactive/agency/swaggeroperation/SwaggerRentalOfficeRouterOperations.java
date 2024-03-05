package com.swiftwheelshubreactive.agency.swaggeroperation;

import com.swiftwheelshubreactive.agency.handler.EmployeeHandler;
import com.swiftwheelshubreactive.dto.RentalOfficeRequest;
import com.swiftwheelshubreactive.dto.RentalOfficeResponse;
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

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@RouterOperations(
        {
                @RouterOperation(
                        method = RequestMethod.GET,
                        beanClass = EmployeeHandler.class,
                        beanMethod = "findAllRentalOffices",
                        operation = @Operation(
                                operationId = "findAllRentalOffices",
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(schema = @Schema(implementation = List.class))
                                        ),
                                        @ApiResponse(
                                                responseCode = "400",
                                                description = "Bad Request"
                                        ),
                                        @ApiResponse(
                                                responseCode = "500",
                                                description = "Internal Server Error"
                                        )
                                }
                        )
                ),
                @RouterOperation(
                        method = RequestMethod.GET,
                        path = "/office/{name}",
                        beanClass = EmployeeHandler.class,
                        beanMethod = "findRentalOfficesByNameInsensitiveCase",
                        operation = @Operation(
                                operationId = "findRentalOfficesByNameInsensitiveCase",
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(schema = @Schema(implementation = List.class))
                                        ),
                                        @ApiResponse(
                                                responseCode = "400",
                                                description = "Bad Request"
                                        ),
                                        @ApiResponse(
                                                responseCode = "500",
                                                description = "Internal Server Error"
                                        )
                                },
                                parameters = @Parameter(in = ParameterIn.PATH, name = "name")
                        )
                ),
                @RouterOperation(
                        method = RequestMethod.GET,
                        path = "/count",
                        beanClass = EmployeeHandler.class,
                        beanMethod = "countRentalOffices",
                        operation = @Operation(
                                operationId = "countRentalOffices",
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(schema = @Schema(implementation = Long.class))
                                        ),
                                        @ApiResponse(
                                                responseCode = "400",
                                                description = "Bad Request"
                                        ),
                                        @ApiResponse(
                                                responseCode = "500",
                                                description = "Internal Server Error"
                                        )
                                }
                        )
                ),
                @RouterOperation(
                        method = RequestMethod.GET,
                        path = "/{id}",
                        beanClass = EmployeeHandler.class,
                        beanMethod = "findRentalOfficeById",
                        operation = @Operation(
                                operationId = "findRentalOfficeById",
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(schema = @Schema(implementation = RentalOfficeResponse.class))
                                        ),
                                        @ApiResponse(
                                                responseCode = "400",
                                                description = "Bad Request"
                                        ),
                                        @ApiResponse(
                                                responseCode = "500",
                                                description = "Internal Server Error"
                                        )
                                },
                                parameters = @Parameter(in = ParameterIn.PATH, name = "id")
                        )
                ),
                @RouterOperation(
                        method = RequestMethod.POST,
                        beanClass = EmployeeHandler.class,
                        beanMethod = "saveRentalOffice",
                        operation = @Operation(
                                operationId = "saveRentalOffice",
                                requestBody = @RequestBody(
                                        description = "Save new rental office",
                                        required = true,
                                        content = @Content(schema = @Schema(implementation = RentalOfficeRequest.class))
                                ),
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(schema = @Schema(implementation = RentalOfficeResponse.class))
                                        ),
                                        @ApiResponse(
                                                responseCode = "400",
                                                description = "Bad Request"
                                        ),
                                        @ApiResponse(
                                                responseCode = "500",
                                                description = "Internal Server Error"
                                        )
                                }
                        )
                ),
                @RouterOperation(
                        method = RequestMethod.PUT,
                        path = "/{id}",
                        beanClass = EmployeeHandler.class,
                        beanMethod = "updateRentalOffice",
                        operation = @Operation(
                                operationId = "updateRentalOffice",
                                requestBody = @RequestBody(
                                        description = "Update rental office",
                                        required = true,
                                        content = @Content(schema = @Schema(implementation = RentalOfficeRequest.class))
                                ),
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(schema = @Schema(implementation = RentalOfficeResponse.class))
                                        ),
                                        @ApiResponse(
                                                responseCode = "400",
                                                description = "Bad Request"
                                        ),
                                        @ApiResponse(
                                                responseCode = "500",
                                                description = "Internal Server Error"
                                        )
                                },
                                parameters = @Parameter(in = ParameterIn.PATH, name = "id")
                        )
                ),
                @RouterOperation(
                        method = RequestMethod.DELETE,
                        path = "/{id}",
                        beanClass = EmployeeHandler.class,
                        beanMethod = "deleteRentalOfficeById",
                        operation = @Operation(
                                operationId = "deleteRentalOfficeById",
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful"
                                        ),
                                        @ApiResponse(
                                                responseCode = "400",
                                                description = "Bad Request"
                                        ),
                                        @ApiResponse(
                                                responseCode = "500",
                                                description = "Internal Server Error"
                                        ),
                                },
                                parameters = @Parameter(in = ParameterIn.PATH, name = "id")
                        )
                )
        }
)
public @interface SwaggerRentalOfficeRouterOperations {
}
