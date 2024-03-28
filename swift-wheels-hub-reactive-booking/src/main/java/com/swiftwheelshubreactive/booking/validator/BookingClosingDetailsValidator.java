package com.swiftwheelshubreactive.booking.validator;

import com.swiftwheelshubreactive.dto.BookingClosingDetails;
import com.swiftwheelshubreactive.lib.validator.AbstractBodyValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

@Component
public class BookingClosingDetailsValidator extends AbstractBodyValidator<BookingClosingDetails, Validator> {

    public BookingClosingDetailsValidator(@Autowired Validator validator) {
        super(BookingClosingDetails.class, validator);
    }

}
