package com.swiftwheelshubreactive.agency.validator;

import com.swiftwheelshubreactive.dto.RentalOfficeRequest;
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
public class RentalOfficeRequestValidator {

    private final Validator validator;

    public final Mono<RentalOfficeRequest> validateBody(RentalOfficeRequest body) {
        return Mono.just(getErrors(body))
                .map(errors -> {
                    if (ObjectUtils.isEmpty(errors) || errors.getAllErrors().isEmpty()) {
                        return body;
                    }

                    throw new SwiftWheelsHubResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            errors.toString()
                    );
                });
    }

    private Errors getErrors(RentalOfficeRequest body) {
        Errors errors = new BeanPropertyBindingResult(body, body.getClass().getName());
        validator.validate(body, errors);

        return errors;
    }

}
