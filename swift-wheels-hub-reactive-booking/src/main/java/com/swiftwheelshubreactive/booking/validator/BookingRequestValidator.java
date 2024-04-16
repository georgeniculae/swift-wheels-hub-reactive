package com.swiftwheelshubreactive.booking.validator;

import com.swiftwheelshubreactive.dto.BookingRequest;
import com.swiftwheelshubreactive.lib.validator.BodyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BookingRequestValidator {

    private final BodyValidator<BookingRequest> validator;

    public final Mono<BookingRequest> validateBody(BookingRequest bookingRequest) {
        return validator.validateBody(bookingRequest);
    }

}
