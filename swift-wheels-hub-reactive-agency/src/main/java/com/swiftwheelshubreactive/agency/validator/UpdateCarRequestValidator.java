package com.swiftwheelshubreactive.agency.validator;

import com.swiftwheelshubreactive.dto.UpdateCarRequest;
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
public class UpdateCarRequestValidator {

    private final Validator validator;

    public final Mono<UpdateCarRequest> validateBody(UpdateCarRequest updateCarRequest) {
        return Mono.just(getErrors(updateCarRequest))
                .map(errors -> {
                    if (ObjectUtils.isEmpty(errors) || errors.getAllErrors().isEmpty()) {
                        return updateCarRequest;
                    }

                    throw new SwiftWheelsHubResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            errors.toString()
                    );
                });
    }

    private Errors getErrors(UpdateCarRequest updateCarRequest) {
        Errors errors = new BeanPropertyBindingResult(updateCarRequest, updateCarRequest.getClass().getName());
        validator.validate(updateCarRequest, errors);

        return errors;
    }

}
