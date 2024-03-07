package com.swiftwheelshubreactive.agency.validator;

import com.swiftwheelshubreactive.dto.CarUpdateDetails;
import com.swiftwheelshubreactive.lib.handlervalidator.AbstractValidationHandler;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

@Component
public class CarUpdateDetailsValidator extends AbstractValidationHandler<CarUpdateDetails, Validator> {

    protected CarUpdateDetailsValidator(Class<CarUpdateDetails> clazz, Validator validator) {
        super(clazz, validator);
    }

}
