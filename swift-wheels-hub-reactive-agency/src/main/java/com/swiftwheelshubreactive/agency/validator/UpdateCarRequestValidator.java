package com.swiftwheelshubreactive.agency.validator;

import com.swiftwheelshubreactive.dto.UpdateCarRequest;
import com.swiftwheelshubreactive.lib.handlervalidator.AbstractValidationHandler;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

@Component
public class UpdateCarRequestValidator extends AbstractValidationHandler<UpdateCarRequest, Validator> {

    protected UpdateCarRequestValidator(Class<UpdateCarRequest> clazz, Validator validator) {
        super(clazz, validator);
    }

}
