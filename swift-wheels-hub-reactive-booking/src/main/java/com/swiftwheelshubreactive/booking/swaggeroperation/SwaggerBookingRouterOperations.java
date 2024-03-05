package com.swiftwheelshubreactive.booking.swaggeroperation;

import com.swiftwheelshubreactive.booking.handler.BookingHandler;
import com.swiftwheelshubreactive.dto.BookingRequest;
import com.swiftwheelshubreactive.dto.BookingResponse;
import io.swagger.v3.oas.annotations.Operation;
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

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@RouterOperations(
        {
                @RouterOperation(
                        method = RequestMethod.GET,
                        path = "/bookings/list",
                        beanClass = BookingHandler.class,
                        beanMethod = "findAllBookings",
                        operation = @Operation(
                                operationId = "findAllBookings",
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(
                                                        array = @ArraySchema(schema = @Schema(implementation = BookingResponse.class)),
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
                                        ),
                                }
                        )
                ),
                @RouterOperation(
                        method = RequestMethod.GET,
                        path = "/bookings/date/{date}",
                        beanClass = BookingHandler.class,
                        beanMethod = "findBookingsByDateOfBooking",
                        operation = @Operation(
                                operationId = "findBookingsByDateOfBooking",
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(
                                                        array = @ArraySchema(schema = @Schema(implementation = BookingResponse.class)),
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
                                        ),
                                }
                        )
                ),
                @RouterOperation(
                        method = RequestMethod.GET,
                        path = "/bookings/count",
                        beanClass = BookingHandler.class,
                        beanMethod = "countBookings",
                        operation = @Operation(
                                operationId = "countBookings",
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
                                        ),
                                }
                        )
                ),
                @RouterOperation(
                        method = RequestMethod.GET,
                        path = "/bookings/count-by-logged-in-user",
                        beanClass = BookingHandler.class,
                        beanMethod = "countBookingsOfLoggedInUser",
                        operation = @Operation(
                                operationId = "countBookingsOfLoggedInUser",
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
                                        ),
                                }
                        )
                ),
                @RouterOperation(
                        method = RequestMethod.GET,
                        path = "/bookings/current-date",
                        beanClass = BookingHandler.class,
                        beanMethod = "getCurrentDate"
                ),
                @RouterOperation(
                        method = RequestMethod.GET,
                        path = "/bookings/bookings-amount-sum",
                        beanClass = BookingHandler.class,
                        beanMethod = "getSumOfAllBookingAmount"
                ),
                @RouterOperation(
                        method = RequestMethod.GET,
                        path = "/bookings/amount-by-user",
                        beanClass = BookingHandler.class,
                        beanMethod = "getAmountSpentByLoggedInUser"
                ),
                @RouterOperation(
                        method = RequestMethod.GET,
                        path = "/bookings/by-current-user",
                        beanClass = BookingHandler.class,
                        beanMethod = "findBookingsByLoggedInUser"
                ),
                @RouterOperation(
                        method = RequestMethod.GET,
                        path = "/bookings/{id}",
                        beanClass = BookingHandler.class,
                        beanMethod = "findBookingById"
                ),
                @RouterOperation(
                        method = RequestMethod.POST,
                        path = "/bookings/new",
                        beanClass = BookingHandler.class,
                        beanMethod = "saveBooking",
                        operation = @Operation(
                                description = "Create new booking",
                                operationId = "saveBooking", tags = "Add new booking",
                                requestBody = @RequestBody(
                                        description = "Create new booking",
                                        required = true,
                                        content = @Content(schema = @Schema(implementation = BookingRequest.class))
                                ),
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(schema = @Schema(implementation = BookingResponse.class))
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
                        path = "/bookings/close-booking",
                        beanClass = BookingHandler.class,
                        beanMethod = "closeBooking"
                ),
                @RouterOperation(
                        method = RequestMethod.PUT,
                        path = "/bookings/{id}",
                        beanClass = BookingHandler.class,
                        beanMethod = "updateBooking"
                ),
                @RouterOperation(
                        method = RequestMethod.DELETE,
                        path = "/bookings/{id}",
                        beanClass = BookingHandler.class,
                        beanMethod = "deleteBookingById"
                )
        }
)
public @interface SwaggerBookingRouterOperations {
}
