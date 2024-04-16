package com.swiftwheelshubreactive.agency.validator;

import com.swiftwheelshubreactive.dto.RentalOfficeRequest;
import com.swiftwheelshubreactive.lib.validator.BodyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RentalOfficeRequestValidator {

    private final BodyValidator<RentalOfficeRequest> validator;

    public final Mono<RentalOfficeRequest> validateBody(RentalOfficeRequest rentalOfficeRequest) {
        return validator.validateBody(rentalOfficeRequest);
    }

}
