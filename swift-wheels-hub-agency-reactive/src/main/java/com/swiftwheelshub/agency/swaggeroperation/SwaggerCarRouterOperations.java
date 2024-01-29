package com.swiftwheelshub.agency.swaggeroperation;

import com.swiftwheelshub.agency.service.CarService;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@RouterOperations(
        {
                @RouterOperation(beanClass = CarService.class, beanMethod = "findAllCars"),
                @RouterOperation(path = "/make/{make}", beanClass = CarService.class, beanMethod = "findCarsByMake"),
                @RouterOperation(path = "/filter/{filter}", beanClass = CarService.class, beanMethod = "findCarsByFilterInsensitiveCase"),
                @RouterOperation(path = "/count", beanClass = CarService.class, beanMethod = "countCars"),
                @RouterOperation(path = "/{id}/availability", beanClass = CarService.class, beanMethod = "getAvailableCar"),
                @RouterOperation(path = "/{id}", beanClass = CarService.class, beanMethod = "findCarById"),
                @RouterOperation(beanClass = CarService.class, beanMethod = "saveCar"),
                @RouterOperation(path = "/update-statuses", beanClass = CarService.class, beanMethod = "updateCarsStatus"),
                @RouterOperation(path = "/{id}/set-car-not-available", beanClass = CarService.class, beanMethod = "updateCarStatus"),
                @RouterOperation(path = "/{id}/update-after-return", beanClass = CarService.class, beanMethod = "updateCarWhenBookingIsClosed"),
                @RouterOperation(path = "/{id}", beanClass = CarService.class, beanMethod = "updateCar"),
                @RouterOperation(path = "/{id}", beanClass = CarService.class, beanMethod = "deleteCarById"),
        }
)
public @interface SwaggerCarRouterOperations {
}
