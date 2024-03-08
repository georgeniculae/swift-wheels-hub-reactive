package com.swiftwheelshubreactive.agency.validator;

import com.swiftwheelshubreactive.dto.CarUpdateDetails;
import com.swiftwheelshubreactive.lib.handlervalidator.AbstractValidationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

@Component
public class CarUpdateDetailsValidator extends AbstractValidationHandler<CarUpdateDetails, Validator> {

    private CarUpdateDetailsValidator(@Autowired Validator validator) {
        super(CarUpdateDetails.class, validator);
    }

}
