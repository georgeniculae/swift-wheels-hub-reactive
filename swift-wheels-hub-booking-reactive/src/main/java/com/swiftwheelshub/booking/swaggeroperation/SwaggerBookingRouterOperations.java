package com.swiftwheelshub.booking.swaggeroperation;

import com.swiftwheelshub.booking.handler.BookingHandler;
import com.swiftwheelshub.dto.BookingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
                @RouterOperation(method = RequestMethod.GET, path = "/list", beanClass = BookingHandler.class, beanMethod = "findAllBookings"),
                @RouterOperation(method = RequestMethod.GET, path = "/date/{date}", beanClass = BookingHandler.class, beanMethod = "findBookingsByDateOfBooking"),
                @RouterOperation(method = RequestMethod.GET, path = "/count", beanClass = BookingHandler.class, beanMethod = "countBookings"),
                @RouterOperation(method = RequestMethod.GET, path = "/count-by-logged-in-user", beanClass = BookingHandler.class, beanMethod = "countBookingsOfLoggedInUser"),
                @RouterOperation(method = RequestMethod.GET, path = "/current-date", beanClass = BookingHandler.class, beanMethod = "getCurrentDate"),
                @RouterOperation(method = RequestMethod.GET, path = "/bookings-amount-sum", beanClass = BookingHandler.class, beanMethod = "getSumOfAllBookingAmount"),
                @RouterOperation(method = RequestMethod.GET, path = "/amount-by-user", beanClass = BookingHandler.class, beanMethod = "getAmountSpentByLoggedInUser"),
                @RouterOperation(method = RequestMethod.GET, path = "/by-current-user", beanClass = BookingHandler.class, beanMethod = "findBookingsByLoggedInUser"),
                @RouterOperation(method = RequestMethod.GET, path = "/{id}", beanClass = BookingHandler.class, beanMethod = "findBookingById"),
                @RouterOperation(method = RequestMethod.POST, path = "/new", beanClass = BookingHandler.class, beanMethod = "saveBooking", operation = @Operation(
                        description = "Create new booking",
                        operationId = "saveBooking", tags = "Add new booking",
                        requestBody = @RequestBody(description = "Create new booking",
                                required = true,
                                content = @Content(schema = @Schema(implementation = BookingResponse.class))))),
                @RouterOperation(method = RequestMethod.POST, path = "/close-booking", beanClass = BookingHandler.class, beanMethod = "closeBooking"),
                @RouterOperation(method = RequestMethod.PUT, path = "/{id}", beanClass = BookingHandler.class, beanMethod = "updateBooking"),
                @RouterOperation(method = RequestMethod.DELETE, path = "/{id}", beanClass = BookingHandler.class, beanMethod = "deleteBookingById")
        }
)
public @interface SwaggerBookingRouterOperations {
}
