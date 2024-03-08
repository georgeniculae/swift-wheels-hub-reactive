package com.swiftwheelshubreactive.agency.validator;

import com.swiftwheelshubreactive.dto.UpdateCarRequest;
import com.swiftwheelshubreactive.lib.handlervalidator.AbstractBodyValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

@Component
public class UpdateCarRequestValidator extends AbstractBodyValidator<UpdateCarRequest, Validator> {

    public UpdateCarRequestValidator(@Autowired Validator validator) {
        super(UpdateCarRequest.class, validator);
    }

}
