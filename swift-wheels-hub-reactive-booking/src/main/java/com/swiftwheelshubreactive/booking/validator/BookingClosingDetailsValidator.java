package com.swiftwheelshubreactive.booking.validator;

import com.swiftwheelshubreactive.dto.BookingClosingDetails;
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
public class BookingClosingDetailsValidator {

    private final Validator validator;

    public final Mono<BookingClosingDetails> validateBody(BookingClosingDetails bookingClosingDetails) {
        return Mono.just(getErrors(bookingClosingDetails))
                .map(errors -> {
                    if (ObjectUtils.isEmpty(errors) || errors.getAllErrors().isEmpty()) {
                        return bookingClosingDetails;
                    }

                    throw new SwiftWheelsHubResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            errors.toString()
                    );
                });
    }

    private Errors getErrors(BookingClosingDetails bookingClosingDetails) {
        Errors errors = new BeanPropertyBindingResult(bookingClosingDetails, bookingClosingDetails.getClass().getName());
        validator.validate(bookingClosingDetails, errors);

        return errors;
    }

}
