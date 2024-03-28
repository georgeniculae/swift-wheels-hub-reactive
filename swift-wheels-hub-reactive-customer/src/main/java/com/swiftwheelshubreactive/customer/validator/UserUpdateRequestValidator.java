package com.swiftwheelshubreactive.customer.validator;

import com.swiftwheelshubreactive.dto.UserUpdateRequest;
import com.swiftwheelshubreactive.lib.validator.AbstractBodyValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

@Component
public class UserUpdateRequestValidator extends AbstractBodyValidator<UserUpdateRequest, Validator> {

    public UserUpdateRequestValidator(@Autowired Validator validator) {
        super(UserUpdateRequest.class, validator);
    }

}
