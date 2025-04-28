package com.autohubreactive.booking.swaggeroperation;

import com.autohubreactive.booking.handler.BookingHandler;
import com.autohubreactive.dto.BookingClosingDetails;
import com.autohubreactive.dto.BookingRequest;
import com.autohubreactive.dto.BookingResponse;
import com.autohubreactive.dto.BookingUpdateResponse;
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
import java.math.BigDecimal;
import java.time.LocalDate;

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
                                },
                                parameters = @Parameter(
                                        in = ParameterIn.PATH,
                                        required = true,
                                        name = "date",
                                        content = @Content(schema = @Schema(implementation = String.class))
                                )
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
                        beanMethod = "getCurrentDate",
                        operation = @Operation(
                                operationId = "getCurrentDate",
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(schema = @Schema(implementation = LocalDate.class))
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
                        path = "/bookings/bookings-amount-sum",
                        beanClass = BookingHandler.class,
                        beanMethod = "getSumOfAllBookingAmount",
                        operation = @Operation(
                                operationId = "getSumOfAllBookingAmount",
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(schema = @Schema(implementation = BigDecimal.class))
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
                        path = "/bookings/amount-by-user",
                        beanClass = BookingHandler.class,
                        beanMethod = "getAmountSpentByLoggedInUser",
                        operation = @Operation(
                                operationId = "getAmountSpentByLoggedInUser",
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(schema = @Schema(implementation = BigDecimal.class))
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
                        path = "/bookings/by-current-user",
                        beanClass = BookingHandler.class,
                        beanMethod = "findBookingsByLoggedInUser",
                        operation = @Operation(
                                operationId = "findBookingsByLoggedInUser",
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
                        path = "/bookings/{id}",
                        beanClass = BookingHandler.class,
                        beanMethod = "findBookingById",
                        operation = @Operation(
                                operationId = "findBookingById",
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
                                        ),
                                },
                                parameters = @Parameter(
                                        in = ParameterIn.PATH,
                                        required = true,
                                        name = "id",
                                        content = @Content(schema = @Schema(implementation = String.class))
                                )
                        )
                ),
                @RouterOperation(
                        method = RequestMethod.POST,
                        path = "/bookings/new",
                        beanClass = BookingHandler.class,
                        beanMethod = "saveBooking",
                        operation = @Operation(
                                description = "Create new booking",
                                operationId = "saveBooking",
                                tags = "Add new booking",
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
                        beanMethod = "closeBooking",
                        operation = @Operation(
                                description = "Close booking",
                                operationId = "closeBooking",
                                tags = "Close booking",
                                requestBody = @RequestBody(
                                        description = "Close booking",
                                        required = true,
                                        content = @Content(schema = @Schema(implementation = BookingClosingDetails.class))
                                ),
                                responses = {
                                        @ApiResponse(
                                                responseCode = "200",
                                                description = "Successful",
                                                content = @Content(schema = @Schema(implementation = BookingUpdateResponse.class))
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
                        path = "/bookings/{id}",
                        beanClass = BookingHandler.class,
                        beanMethod = "updateBooking",
                        operation = @Operation(
                                description = "Update booking",
                                operationId = "Update booking",
                                tags = "Update booking",
                                requestBody = @RequestBody(
                                        description = "Update booking",
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
                                },
                                parameters = @Parameter(
                                        in = ParameterIn.PATH,
                                        required = true,
                                        name = "id",
                                        content = @Content(schema = @Schema(implementation = String.class))
                                )
                        )
                )
        }
)
public @interface SwaggerBookingRouterOperations {
}
