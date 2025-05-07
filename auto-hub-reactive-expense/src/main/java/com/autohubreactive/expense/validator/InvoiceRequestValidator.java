package com.autohubreactive.expense.validator;

import com.autohubreactive.dto.invoice.InvoiceRequest;
import com.autohubreactive.lib.validator.BodyValidator;
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
