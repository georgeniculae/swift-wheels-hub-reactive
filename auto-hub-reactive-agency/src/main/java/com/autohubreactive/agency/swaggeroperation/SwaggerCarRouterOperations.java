package com.autohubreactive.agency.swaggeroperation;

import com.autohubreactive.agency.handler.CarHandler;
import com.autohubreactive.dto.agency.CarRequest;
import com.autohubreactive.dto.agency.CarResponse;
import com.autohubreactive.dto.common.AvailableCarInfo;
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
import org.springframework.http.codec.multipart.FilePart;
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
                        path = "/cars",
                        beanClass = CarHandler.class,
                        beanMethod = "findAllCars",
                        operation = @Operation(
                                operationId = "findAllCars",
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(
                                                        array = @ArraySchema(schema = @Schema(implementation = CarResponse.class)),
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
                        path = "/cars/make/{make}",
                        beanClass = CarHandler.class,
                        beanMethod = "findCarsByMakeInsensitiveCase",
                        operation = @Operation(
                                operationId = "findCarsByMakeInsensitiveCase",
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(schema = @Schema(implementation = CarResponse.class))
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
                                        name = "make",
                                        in = ParameterIn.PATH,
                                        required = true,
                                        content = @Content(schema = @Schema(implementation = String.class))
                                )
                        )
                ),
                @RouterOperation(
                        method = RequestMethod.GET,
                        path = "/cars/filter/{filter}",
                        beanClass = CarHandler.class,
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
                        path = "/cars/count",
                        beanClass = CarHandler.class,
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
                        path = "/cars/{id}/availability",
                        beanClass = CarHandler.class,
                        beanMethod = "getAvailableCar",
                        operation = @Operation(
                                operationId = "getAvailableCar",
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(schema = @Schema(implementation = AvailableCarInfo.class))
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
                        path = "/available",
                        beanClass = CarHandler.class,
                        beanMethod = "findAllAvailableCars",
                        operation = @Operation(
                                operationId = "findAllAvailableCars",
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(
                                                        array = @ArraySchema(schema = @Schema(implementation = CarResponse.class)),
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
                        path = "/cars/{id}",
                        beanClass = CarHandler.class,
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
                        path = "/cars/{id}/image",
                        beanClass = CarHandler.class,
                        beanMethod = "getCarImage",
                        operation = @Operation(
                                operationId = "getCarImage",
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(
                                                        schema = @Schema(implementation = byte[].class),
                                                        mediaType = MediaType.MULTIPART_FORM_DATA_VALUE
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
                        method = RequestMethod.POST,
                        path = "/cars",
                        beanClass = CarHandler.class,
                        beanMethod = "saveCar",
                        operation = @Operation(
                                operationId = "saveCar",
                                requestBody = @RequestBody(
                                        description = "Save new car",
                                        required = true,
                                        content = {
                                                @Content(
                                                        schema = @Schema(implementation = CarRequest.class),
                                                        mediaType = MediaType.APPLICATION_JSON_VALUE
                                                ),
                                                @Content(
                                                        schema = @Schema(implementation = FilePart.class),
                                                        mediaType = MediaType.MULTIPART_FORM_DATA_VALUE
                                                )
                                        }
                                ),
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(
                                                        schema = @Schema(implementation = CarResponse.class),
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
                        method = RequestMethod.POST,
                        path = "/cars/upload",
                        beanClass = CarHandler.class,
                        beanMethod = "uploadCars",
                        operation = @Operation(
                                operationId = "uploadCars",
                                requestBody = @RequestBody(
                                        description = "Upload cars",
                                        required = true,
                                        content = @Content(
                                                schema = @Schema(implementation = FilePart.class),
                                                mediaType = MediaType.MULTIPART_FORM_DATA_VALUE
                                        )
                                ),
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(
                                                        array = @ArraySchema(schema = @Schema(implementation = CarResponse.class)),
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
                        method = RequestMethod.PUT,
                        path = "/cars/{id}",
                        beanClass = CarHandler.class,
                        beanMethod = "updateCar",
                        operation = @Operation(
                                operationId = "updateCar",
                                requestBody = @RequestBody(
                                        description = "Update car",
                                        required = true,
                                        content = {
                                                @Content(
                                                        schema = @Schema(implementation = CarRequest.class),
                                                        mediaType = MediaType.MULTIPART_FORM_DATA_VALUE
                                                ),
                                                @Content(
                                                        schema = @Schema(implementation = FilePart.class),
                                                        mediaType = MediaType.MULTIPART_FORM_DATA_VALUE
                                                )
                                        }
                                ),
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(
                                                        schema = @Schema(implementation = CarResponse.class),
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
                        method = RequestMethod.DELETE,
                        path = "/cars/{id}",
                        beanClass = CarHandler.class,
                        beanMethod = "deleteCarById",
                        operation = @Operation(
                                operationId = "deleteCarById",
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
        }
)
public @interface SwaggerCarRouterOperations {
}
