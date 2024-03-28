package com.swiftwheelshubreactive.agency.validator;

import com.swiftwheelshubreactive.dto.BranchRequest;
import com.swiftwheelshubreactive.lib.validator.AbstractBodyValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

@Component
public class BranchRequestValidator extends AbstractBodyValidator<BranchRequest, Validator> {

    public BranchRequestValidator(@Autowired Validator validator) {
        super(BranchRequest.class, validator);
    }

}
