package com.swiftwheelshubreactive.lib.handlervalidator;

import com.swiftwheelshubreactive.exception.SwiftWheelsHubResponseStatusException;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import reactor.core.publisher.Mono;

public abstract class AbstractValidationHandler<T, U extends Validator> {

    private final Class<T> validationClass;

    private final U validator;

    protected AbstractValidationHandler(Class<T> clazz, U validator) {
        this.validationClass = clazz;
        this.validator = validator;
    }

    public final Mono<T> handleRequest(T body) {
        return Mono.fromSupplier(() -> {
            Errors errors = new BeanPropertyBindingResult(body, validationClass.getName());
            validator.validate(body, errors);

            if (ObjectUtils.isEmpty(errors) || errors.getAllErrors().isEmpty()) {
                return body;
            } else {
                throw new SwiftWheelsHubResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        errors.toString()
                );
            }
        });
    }

}
