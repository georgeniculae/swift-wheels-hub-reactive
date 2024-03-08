package com.swiftwheelshubreactive.agency.validator;

import com.swiftwheelshubreactive.dto.EmployeeRequest;
import com.swiftwheelshubreactive.lib.handlervalidator.AbstractValidationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

@Component
public class EmployeeRequestValidator extends AbstractValidationHandler<EmployeeRequest, Validator> {

    public EmployeeRequestValidator(@Autowired Validator validator) {
        super(EmployeeRequest.class, validator);
    }

}
