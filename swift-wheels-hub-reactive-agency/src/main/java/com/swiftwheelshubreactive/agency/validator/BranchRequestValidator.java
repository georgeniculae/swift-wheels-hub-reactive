package com.swiftwheelshubreactive.agency.validator;

import com.swiftwheelshubreactive.dto.BranchRequest;
import com.swiftwheelshubreactive.lib.handlervalidator.AbstractValidationHandler;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

@Component
public class BranchRequestValidator extends AbstractValidationHandler<BranchRequest, Validator> {

    public BranchRequestValidator(Validator validator) {
        super(BranchRequest.class, validator);
    }

}
