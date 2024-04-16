package com.swiftwheelshubreactive.agency.validator;

import com.swiftwheelshubreactive.dto.UpdateCarRequest;
import com.swiftwheelshubreactive.lib.validator.BodyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UpdateCarRequestValidator {

    private final BodyValidator<UpdateCarRequest> validator;

    public final Mono<UpdateCarRequest> validateBody(UpdateCarRequest updateCarRequest) {
        return validator.validateBody(updateCarRequest);
    }

}
