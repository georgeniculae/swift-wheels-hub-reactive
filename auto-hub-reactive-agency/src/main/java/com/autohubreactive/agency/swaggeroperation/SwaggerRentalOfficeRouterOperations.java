package com.autohubreactive.agency.swaggeroperation;

import com.autohubreactive.agency.handler.RentalOfficeHandler;
import com.autohubreactive.dto.agency.RentalOfficeRequest;
import com.autohubreactive.dto.agency.RentalOfficeResponse;
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
                        path = "/rental-offices",
                        beanClass = RentalOfficeHandler.class,
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
                        path = "/rental-offices/filter/{filter}",
                        beanClass = RentalOfficeHandler.class,
                        beanMethod = "findRentalOfficesByFilterInsensitiveCase",
                        operation = @Operation(
                                operationId = "findRentalOfficesByFilterInsensitiveCase",
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(schema = @Schema(implementation = List.class))
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
                        path = "/rental-offices/count",
                        beanClass = RentalOfficeHandler.class,
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
                        path = "/rental-offices/{id}",
                        beanClass = RentalOfficeHandler.class,
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
                        path = "/rental-offices",
                        beanClass = RentalOfficeHandler.class,
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
                        method = RequestMethod.PUT,
                        path = "/rental-offices/{id}",
                        beanClass = RentalOfficeHandler.class,
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
                        method = RequestMethod.DELETE,
                        path = "/rental-offices/{id}",
                        beanClass = RentalOfficeHandler.class,
                        beanMethod = "deleteRentalOfficeById",
                        operation = @Operation(
                                operationId = "deleteRentalOfficeById",
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
public @interface SwaggerRentalOfficeRouterOperations {
}
