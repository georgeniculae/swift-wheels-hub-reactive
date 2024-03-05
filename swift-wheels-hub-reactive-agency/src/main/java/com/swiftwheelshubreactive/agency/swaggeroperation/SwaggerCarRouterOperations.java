package com.swiftwheelshubreactive.agency.swaggeroperation;

import com.swiftwheelshubreactive.agency.service.CarService;
import com.swiftwheelshubreactive.dto.CarRequest;
import com.swiftwheelshubreactive.dto.CarResponse;
import com.swiftwheelshubreactive.dto.CarState;
import com.swiftwheelshubreactive.dto.CarUpdateDetails;
import com.swiftwheelshubreactive.dto.UpdateCarRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.http.codec.multipart.FilePart;
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
                        beanClass = CarService.class,
                        beanMethod = "findAllCars",
                        operation = @Operation(
                                operationId = "findAllCars",
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
                        path = "/make/{make}",
                        params = "make",
                        beanClass = CarService.class,
                        beanMethod = "findCarsByMake",
                        operation = @Operation(
                                operationId = "findAllCars",
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(schema = @Schema(implementation = CarResponse.class))
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
                                parameters = @Parameter(in = ParameterIn.PATH, name = "make")
                        )
                ),
                @RouterOperation(
                        method = RequestMethod.GET,
                        path = "/filter/{filter}",
                        params = "filter",
                        beanClass = CarService.class,
                        beanMethod = "findCarsByFilterInsensitiveCase",
                        operation = @Operation(
                                operationId = "findCarsByFilterInsensitiveCase",
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(schema = @Schema(implementation = CarResponse.class))
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
                        beanClass = CarService.class,
                        beanMethod = "countCars",
                        operation = @Operation(
                                operationId = "countCars",
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
                        path = "/{id}/availability",
                        params = "id", beanClass = CarService.class,
                        beanMethod = "getAvailableCar",
                        operation = @Operation(
                                operationId = "getAvailableCar",
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(schema = @Schema(implementation = CarResponse.class))
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
                        method = RequestMethod.GET,
                        path = "/{id}", params = "id",
                        beanClass = CarService.class,
                        beanMethod = "findCarById",
                        operation = @Operation(
                                operationId = "findCarById",
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(schema = @Schema(implementation = CarResponse.class))
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
                        beanClass = CarService.class,
                        beanMethod = "saveCar",
                        operation = @Operation(
                                operationId = "saveCar",
                                requestBody = @RequestBody(
                                        description = "Save new car",
                                        required = true,
                                        content = @Content(schema = @Schema(implementation = CarRequest.class))
                                ),
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(schema = @Schema(implementation = CarResponse.class))
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
                        path = "/upload",
                        beanClass = CarService.class,
                        beanMethod = "uploadCars",
                        operation = @Operation(
                                operationId = "uploadCars",
                                requestBody = @RequestBody(
                                        description = "Upload cars",
                                        required = true,
                                        content = @Content(schema = @Schema(implementation = FilePart.class))
                                ),
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
                        method = RequestMethod.PUT,
                        path = "/update-statuses",
                        beanClass = CarService.class,
                        beanMethod = "updateCarsStatus",
                        operation = @Operation(
                                operationId = "updateCarsStatus",
                                requestBody = @RequestBody(
                                        description = "Update cars status",
                                        required = true,
                                        content = @Content(schema = @Schema(implementation = UpdateCarRequest.class))
                                ),
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
                        method = RequestMethod.PUT,
                        path = "/{id}/change-status",
                        params = "id",
                        beanClass = CarService.class,
                        beanMethod = "updateCarStatus",
                        operation = @Operation(
                                operationId = "updateCarStatus",
                                requestBody = @RequestBody(
                                        description = "Update car status",
                                        required = true,
                                        content = @Content(schema = @Schema(implementation = CarState.class))
                                ),
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
                                parameters = @Parameter(in = ParameterIn.PATH, name = "id")
                        )
                ),
                @RouterOperation(
                        method = RequestMethod.PUT,
                        path = "/{id}/update-after-return",
                        params = "id",
                        beanClass = CarService.class,
                        beanMethod = "updateCarWhenBookingIsClosed",
                        operation = @Operation(
                                operationId = "updateCarWhenBookingIsClosed",
                                requestBody = @RequestBody(
                                        description = "Update car status",
                                        required = true,
                                        content = @Content(schema = @Schema(implementation = CarUpdateDetails.class))
                                ),
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(schema = @Schema(implementation = CarResponse.class))
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
                        method = RequestMethod.PUT,
                        path = "/{id}",
                        params = "id",
                        beanClass = CarService.class,
                        beanMethod = "updateCar",
                        operation = @Operation(
                                operationId = "updateCar",
                                requestBody = @RequestBody(
                                        description = "Update car",
                                        required = true,
                                        content = @Content(schema = @Schema(implementation = CarRequest.class))
                                ),
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(schema = @Schema(implementation = CarResponse.class))
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
                        params = "id",
                        beanClass = CarService.class,
                        beanMethod = "deleteCarById",
                        operation = @Operation(
                                operationId = "deleteCarById",
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
                                        )
                                },
                                parameters = @Parameter(in = ParameterIn.PATH, name = "id")
                        )
                ),
        }
)
public @interface SwaggerCarRouterOperations {
}
