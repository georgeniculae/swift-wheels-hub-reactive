package com.swiftwheelshubreactive.booking.validator;

import com.swiftwheelshubreactive.dto.BookingRequest;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubResponseStatusException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BookingRequestValidator {

    private final Validator validator;

    public final Mono<BookingRequest> validateBody(BookingRequest bookingRequest) {
        return Mono.just(getErrors(bookingRequest))
                .map(errors -> {
                    if (ObjectUtils.isEmpty(errors) || errors.getAllErrors().isEmpty()) {
                        return bookingRequest;
                    }

                    throw new SwiftWheelsHubResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            errors.toString()
                    );
                });
    }

    private Errors getErrors(BookingRequest bookingRequest) {
        Errors errors = new BeanPropertyBindingResult(bookingRequest, bookingRequest.getClass().getName());
        validator.validate(bookingRequest, errors);

        return errors;
    }

}
