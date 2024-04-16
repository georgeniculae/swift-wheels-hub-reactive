package com.swiftwheelshubreactive.expense.validator;

import com.swiftwheelshubreactive.dto.InvoiceRequest;
import com.swiftwheelshubreactive.lib.validator.BodyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class InvoiceRequestValidator {

    private final BodyValidator<InvoiceRequest> validator;

    public final Mono<InvoiceRequest> validateBody(InvoiceRequest invoiceRequest) {
        return validator.validateBody(invoiceRequest);
    }

}
