package com.swiftwheelshubreactive.customer.validator;

import com.swiftwheelshubreactive.dto.RegisterRequest;
import com.swiftwheelshubreactive.lib.validator.BodyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RegisterRequestValidator {

    private final BodyValidator<RegisterRequest> validator;

    public Mono<RegisterRequest> validateBody(RegisterRequest registerRequest) {
        return validator.validateBody(registerRequest);
    }

}
