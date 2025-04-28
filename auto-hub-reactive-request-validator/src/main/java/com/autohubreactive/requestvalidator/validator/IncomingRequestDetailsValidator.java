package com.autohubreactive.requestvalidator.validator;

import com.autohubreactive.dto.IncomingRequestDetails;
import com.autohubreactive.lib.validator.BodyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class IncomingRequestDetailsValidator {

    private final BodyValidator<IncomingRequestDetails> incomingRequestDetailsBodyValidator;

    public Mono<IncomingRequestDetails> validateBody(IncomingRequestDetails incomingRequestDetails) {
        return incomingRequestDetailsBodyValidator.validateBody(incomingRequestDetails);
    }

}
