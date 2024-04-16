package com.swiftwheelshubreactive.expense.validator;

import com.swiftwheelshubreactive.dto.InvoiceRequest;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubResponseStatusException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class InvoiceRequestValidator {

    private final Validator validator;

    public final Mono<InvoiceRequest> validateBody(InvoiceRequest invoiceRequest) {
        return Mono.just(getErrors(invoiceRequest))
                .map(errors -> {
                    if (ObjectUtils.isEmpty(errors) || errors.getAllErrors().isEmpty()) {
                        return invoiceRequest;
                    }

                    throw new SwiftWheelsHubResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            errors.toString()
                    );
                });
    }

    private Errors getErrors(InvoiceRequest invoiceRequest) {
        Errors errors = new BeanPropertyBindingResult(invoiceRequest, invoiceRequest.getClass().getName());
        validator.validate(invoiceRequest, errors);

        return errors;
    }


}
