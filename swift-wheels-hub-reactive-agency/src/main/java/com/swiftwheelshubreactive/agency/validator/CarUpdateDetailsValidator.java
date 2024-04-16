package com.swiftwheelshubreactive.agency.validator;

import com.swiftwheelshubreactive.dto.CarUpdateDetails;
import com.swiftwheelshubreactive.lib.validator.BodyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CarUpdateDetailsValidator {

    private final BodyValidator<CarUpdateDetails> validator;

    public final Mono<CarUpdateDetails> validateBody(CarUpdateDetails carUpdateDetails) {
        return validator.validateBody(carUpdateDetails);
    }

}
