package com.autohubreactive.agency.validator;

import com.autohubreactive.dto.agency.CarRequest;
import com.autohubreactive.lib.validator.BodyValidator;
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
