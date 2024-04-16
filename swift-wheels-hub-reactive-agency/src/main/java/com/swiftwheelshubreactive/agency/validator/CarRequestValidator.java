package com.swiftwheelshubreactive.agency.validator;

import com.swiftwheelshubreactive.dto.CarRequest;
import com.swiftwheelshubreactive.lib.validator.BodyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CarRequestValidator {

    private final BodyValidator<CarRequest> validator;

    public final Mono<CarRequest> validateBody(CarRequest carRequest) {
        return validator.validateBody(carRequest);
    }
}
