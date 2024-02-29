package com.swiftwheelshubreactive.agency.swaggeroperation;

import com.swiftwheelshubreactive.agency.service.CarService;
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
                @RouterOperation(method = RequestMethod.GET, beanClass = CarService.class, beanMethod = "findAllCars"),
                @RouterOperation(method = RequestMethod.GET, path = "/make/{make}", beanClass = CarService.class, beanMethod = "findCarsByMake"),
                @RouterOperation(method = RequestMethod.GET, path = "/filter/{filter}", beanClass = CarService.class, beanMethod = "findCarsByFilterInsensitiveCase"),
                @RouterOperation(method = RequestMethod.GET, path = "/count", beanClass = CarService.class, beanMethod = "countCars"),
                @RouterOperation(method = RequestMethod.GET, path = "/{id}/availability", beanClass = CarService.class, beanMethod = "getAvailableCar"),
                @RouterOperation(method = RequestMethod.GET, path = "/{id}", beanClass = CarService.class, beanMethod = "findCarById"),
                @RouterOperation(method = RequestMethod.POST, beanClass = CarService.class, beanMethod = "saveCar"),
                @RouterOperation(method = RequestMethod.POST, path = "/upload", beanClass = CarService.class, beanMethod = "uploadCars"),
                @RouterOperation(method = RequestMethod.PUT, path = "/update-statuses", beanClass = CarService.class, beanMethod = "updateCarsStatus"),
                @RouterOperation(method = RequestMethod.PUT, path = "/{id}/set-car-not-available", beanClass = CarService.class, beanMethod = "updateCarStatus"),
                @RouterOperation(method = RequestMethod.PUT, path = "/{id}/update-after-return", beanClass = CarService.class, beanMethod = "updateCarWhenBookingIsClosed"),
                @RouterOperation(method = RequestMethod.PUT, path = "/{id}", beanClass = CarService.class, beanMethod = "updateCar"),
                @RouterOperation(method = RequestMethod.DELETE, path = "/{id}", beanClass = CarService.class, beanMethod = "deleteCarById"),
        }
)
public @interface SwaggerCarRouterOperations {
}
