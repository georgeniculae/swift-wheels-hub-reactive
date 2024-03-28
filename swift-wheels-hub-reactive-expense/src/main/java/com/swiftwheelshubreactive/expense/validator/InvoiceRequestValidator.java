package com.swiftwheelshubreactive.expense.validator;

import com.swiftwheelshubreactive.dto.InvoiceRequest;
import com.swiftwheelshubreactive.lib.validator.AbstractBodyValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

@Component
public class InvoiceRequestValidator extends AbstractBodyValidator<InvoiceRequest, Validator> {

    public InvoiceRequestValidator(@Autowired Validator validator) {
        super(InvoiceRequest.class, validator);
    }

}
