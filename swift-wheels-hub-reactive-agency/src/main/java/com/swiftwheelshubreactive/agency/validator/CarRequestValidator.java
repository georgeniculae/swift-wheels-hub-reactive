package com.swiftwheelshubreactive.agency.validator;

import com.swiftwheelshubreactive.dto.CarRequest;
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
public class CarRequestValidator {

    private final Validator validator;

    public final Mono<CarRequest> validateBody(CarRequest carRequest) {
        return Mono.just(getErrors(carRequest))
                .map(errors -> {
                    if (ObjectUtils.isEmpty(errors) || errors.getAllErrors().isEmpty()) {
                        return carRequest;
                    }

                    throw new SwiftWheelsHubResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            errors.toString()
                    );
                });
    }

    private Errors getErrors(CarRequest carRequest) {
        Errors errors = new BeanPropertyBindingResult(carRequest, carRequest.getClass().getName());
        validator.validate(carRequest, errors);

        return errors;
    }
}
