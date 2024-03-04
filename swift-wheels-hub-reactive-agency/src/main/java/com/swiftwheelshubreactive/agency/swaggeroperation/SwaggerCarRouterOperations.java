package com.swiftwheelshubreactive.agency.swaggeroperation;

import com.swiftwheelshubreactive.agency.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
                                responses = @ApiResponse(

                                )
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
                                responses = @ApiResponse(

                                ),
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
                                responses = @ApiResponse(

                                ),
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
                                responses = @ApiResponse(

                                )
                        )
                ),
                @RouterOperation(
                        method = RequestMethod.GET,
                        path = "/{id}/availability",
                        params = "id", beanClass = CarService.class,
                        beanMethod = "getAvailableCar"
                ),
                @RouterOperation(
                        method = RequestMethod.GET,
                        path = "/{id}", params = "id",
                        beanClass = CarService.class,
                        beanMethod = "findCarById"
                ),
                @RouterOperation(
                        method = RequestMethod.POST,
                        beanClass = CarService.class,
                        beanMethod = "saveCar"
                ),
                @RouterOperation(
                        method = RequestMethod.POST,
                        path = "/upload",
                        beanClass = CarService.class,
                        beanMethod = "uploadCars"
                ),
                @RouterOperation(
                        method = RequestMethod.PUT,
                        path = "/update-statuses",
                        beanClass = CarService.class,
                        beanMethod = "updateCarsStatus"
                ),
                @RouterOperation(
                        method = RequestMethod.PUT,
                        path = "/{id}/set-car-not-available",
                        params = "id",
                        beanClass = CarService.class,
                        beanMethod = "updateCarStatus"
                ),
                @RouterOperation(
                        method = RequestMethod.PUT,
                        path = "/{id}/update-after-return",
                        params = "id",
                        beanClass = CarService.class,
                        beanMethod = "updateCarWhenBookingIsClosed"
                ),
                @RouterOperation(
                        method = RequestMethod.PUT,
                        path = "/{id}",
                        params = "id",
                        beanClass = CarService.class,
                        beanMethod = "updateCar"
                ),
                @RouterOperation(
                        method = RequestMethod.DELETE,
                        path = "/{id}",
                        params = "id",
                        beanClass = CarService.class,
                        beanMethod = "deleteCarById"
                ),
        }
)
public @interface SwaggerCarRouterOperations {
}
