package com.swiftwheelshubreactive.agency.validator;

import com.swiftwheelshubreactive.dto.CarRequest;
import com.swiftwheelshubreactive.lib.validator.AbstractBodyValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

@Component
public class CarRequestValidator extends AbstractBodyValidator<CarRequest, Validator> {

    public CarRequestValidator(@Autowired Validator validator) {
        super(CarRequest.class, validator);
    }

}
