package com.swiftwheelshubreactive.agency.swaggeroperation;

import com.swiftwheelshubreactive.agency.service.EmployeeService;
import com.swiftwheelshubreactive.dto.EmployeeRequest;
import com.swiftwheelshubreactive.dto.EmployeeResponse;
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
                        beanClass = EmployeeService.class,
                        beanMethod = "findAllEmployees",
                        operation = @Operation(
                                operationId = "findAllEmployees",
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
                        path = "/branch/{id}",
                        params = "id",
                        beanClass = EmployeeService.class,
                        beanMethod = "findEmployeesByBranchId",
                        operation = @Operation(
                                operationId = "findEmployeesByBranchId",
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
                                parameters = @Parameter(in = ParameterIn.PATH, name = "filter")
                        )
                ),
                @RouterOperation(
                        method = RequestMethod.GET,
                        path = "/filter/{filter}",
                        params = "filter",
                        beanClass = EmployeeService.class,
                        beanMethod = "findEmployeeByFilterInsensitiveCase",
                        operation = @Operation(
                                operationId = "findEmployeeByFilterInsensitiveCase",
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
                                parameters = @Parameter(in = ParameterIn.PATH, name = "filter")
                        )
                ),
                @RouterOperation(
                        method = RequestMethod.GET,
                        path = "/count",
                        beanClass = EmployeeService.class,
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
                        params = "id",
                        beanClass = EmployeeService.class,
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
                        method = RequestMethod.POST,
                        beanClass = EmployeeService.class,
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
                                                description = "Bad Request"
                                        ),
                                        @ApiResponse(
                                                responseCode = "500",
                                                description = "Internal Server Error"
                                        ),
                                }
                        )
                ),
                @RouterOperation(
                        method = RequestMethod.PUT,
                        beanClass = EmployeeService.class,
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
                                                description = "Bad Request"
                                        ),
                                        @ApiResponse(
                                                responseCode = "500",
                                                description = "Internal Server Error"
                                        ),
                                },
                                parameters = @Parameter(in = ParameterIn.PATH, name = "id")
                        )
                ),
                @RouterOperation(
                        method = RequestMethod.DELETE,
                        params = "id",
                        beanClass = EmployeeService.class,
                        beanMethod = "deleteEmployeeById",
                        operation = @Operation(
                                operationId = "deleteEmployeeById",
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
public @interface SwaggerEmployeeRouterOperations {
}
