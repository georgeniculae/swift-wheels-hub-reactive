package com.autohubreactive.agency.swaggeroperation;

import com.autohubreactive.agency.handler.EmployeeHandler;
import com.autohubreactive.dto.EmployeeRequest;
import com.autohubreactive.dto.EmployeeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RouterOperations(
        {
                @RouterOperation(
                        method = RequestMethod.GET,
                        path = "/employees",
                        beanClass = EmployeeHandler.class,
                        beanMethod = "findAllEmployees",
                        operation = @Operation(
                                operationId = "findAllEmployees",
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(
                                                        array = @ArraySchema(schema = @Schema(implementation = EmployeeResponse.class)),
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
                        path = "/employees/branch/{id}",
                        beanClass = EmployeeHandler.class,
                        beanMethod = "findEmployeesByBranchId",
                        operation = @Operation(
                                operationId = "findEmployeesByBranchId",
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(
                                                        array = @ArraySchema(schema = @Schema(implementation = EmployeeResponse.class)),
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
                                        name = "id",
                                        in = ParameterIn.PATH,
                                        required = true,
                                        content = @Content(schema = @Schema(implementation = String.class))
                                )
                        )
                ),
                @RouterOperation(
                        method = RequestMethod.GET,
                        path = "/employees/filter/{filter}",
                        beanClass = EmployeeHandler.class,
                        beanMethod = "findEmployeeByFilterInsensitiveCase",
                        operation = @Operation(
                                operationId = "findEmployeeByFilterInsensitiveCase",
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(
                                                        array = @ArraySchema(schema = @Schema(implementation = EmployeeResponse.class)),
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
                                        name = "filter",
                                        in = ParameterIn.PATH,
                                        required = true,
                                        content = @Content(schema = @Schema(implementation = String.class))
                                )
                        )
                ),
                @RouterOperation(
                        method = RequestMethod.GET,
                        path = "/employees/count",
                        beanClass = EmployeeHandler.class,
                        beanMethod = "countEmployees",
                        operation = @Operation(
                                operationId = "countEmployees",
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(schema = @Schema(implementation = Long.class))
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
                        path = "/employees/{id}",
                        beanClass = EmployeeHandler.class,
                        beanMethod = "findEmployeeById",
                        operation = @Operation(
                                operationId = "findEmployeeById",
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(schema = @Schema(implementation = EmployeeResponse.class))
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
                                        name = "id",
                                        in = ParameterIn.PATH,
                                        required = true,
                                        content = @Content(schema = @Schema(implementation = String.class))
                                )
                        )
                ),
                @RouterOperation(
                        method = RequestMethod.POST,
                        path = "/employees",
                        beanClass = EmployeeHandler.class,
                        beanMethod = "saveEmployee",
                        operation = @Operation(
                                operationId = "saveEmployee",
                                requestBody = @RequestBody(
                                        description = "Save new employee",
                                        required = true,
                                        content = @Content(schema = @Schema(implementation = EmployeeRequest.class))
                                ),
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(schema = @Schema(implementation = EmployeeResponse.class))
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
                                        ),
                                }
                        )
                ),
                @RouterOperation(
                        method = RequestMethod.PUT,
                        path = "/employees/{id}",
                        beanClass = EmployeeHandler.class,
                        beanMethod = "updateEmployee",
                        operation = @Operation(
                                operationId = "updateEmployee",
                                requestBody = @RequestBody(
                                        description = "Update employee",
                                        required = true,
                                        content = @Content(schema = @Schema(implementation = EmployeeRequest.class))
                                ),
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(schema = @Schema(implementation = EmployeeResponse.class))
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
                                        ),
                                },
                                parameters = @Parameter(
                                        name = "id",
                                        in = ParameterIn.PATH,
                                        required = true,
                                        content = @Content(schema = @Schema(implementation = String.class))
                                )
                        )
                ),
                @RouterOperation(
                        method = RequestMethod.DELETE,
                        path = "/employees/{id}",
                        beanClass = EmployeeHandler.class,
                        beanMethod = "deleteEmployeeById",
                        operation = @Operation(
                                operationId = "deleteEmployeeById",
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(schema = @Schema())
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
                                        ),
                                },
                                parameters = @Parameter(
                                        name = "id",
                                        in = ParameterIn.PATH,
                                        required = true,
                                        content = @Content(schema = @Schema(implementation = String.class))
                                )
                        )
                )
        }
)
public @interface SwaggerEmployeeRouterOperations {
}
