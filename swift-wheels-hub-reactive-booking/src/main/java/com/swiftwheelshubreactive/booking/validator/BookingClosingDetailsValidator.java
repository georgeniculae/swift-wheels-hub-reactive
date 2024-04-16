package com.swiftwheelshubreactive.booking.validator;

import com.swiftwheelshubreactive.dto.BookingClosingDetails;
import com.swiftwheelshubreactive.lib.validator.BodyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BookingClosingDetailsValidator {

    private final BodyValidator<BookingClosingDetails> validator;

    public Mono<BookingClosingDetails> validateBody(BookingClosingDetails bookingClosingDetails) {
        return validator.validateBody(bookingClosingDetails);
    }

}
