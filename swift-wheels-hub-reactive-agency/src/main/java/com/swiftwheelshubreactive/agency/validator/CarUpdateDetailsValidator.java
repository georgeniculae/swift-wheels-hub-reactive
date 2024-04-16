package com.swiftwheelshubreactive.agency.validator;

import com.swiftwheelshubreactive.dto.CarUpdateDetails;
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
public class CarUpdateDetailsValidator {

    private final Validator validator;

    public final Mono<CarUpdateDetails> validateBody(CarUpdateDetails carUpdateDetails) {
        return Mono.just(getErrors(carUpdateDetails))
                .map(errors -> {
                    if (ObjectUtils.isEmpty(errors) || errors.getAllErrors().isEmpty()) {
                        return carUpdateDetails;
                    }

                    throw new SwiftWheelsHubResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            errors.toString()
                    );
                });
    }

    private Errors getErrors(CarUpdateDetails carUpdateDetails) {
        Errors errors = new BeanPropertyBindingResult(carUpdateDetails, carUpdateDetails.getClass().getName());
        validator.validate(carUpdateDetails, errors);

        return errors;
    }

}
