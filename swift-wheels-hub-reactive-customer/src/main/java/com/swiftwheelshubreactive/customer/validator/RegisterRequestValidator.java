package com.swiftwheelshubreactive.customer.validator;

import com.swiftwheelshubreactive.dto.RegisterRequest;
import com.swiftwheelshubreactive.lib.validator.AbstractBodyValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

@Component
public class RegisterRequestValidator extends AbstractBodyValidator<RegisterRequest, Validator> {

    public RegisterRequestValidator(@Autowired Validator validator) {
        super(RegisterRequest.class, validator);
    }

}
