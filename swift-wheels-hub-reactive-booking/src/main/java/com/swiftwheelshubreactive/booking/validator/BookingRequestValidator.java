package com.swiftwheelshubreactive.booking.validator;

import com.swiftwheelshubreactive.dto.BookingRequest;
import com.swiftwheelshubreactive.lib.validator.AbstractBodyValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

@Component
public class BookingRequestValidator extends AbstractBodyValidator<BookingRequest, Validator> {

    public BookingRequestValidator(@Autowired Validator validator) {
        super(BookingRequest.class, validator);
    }

}
