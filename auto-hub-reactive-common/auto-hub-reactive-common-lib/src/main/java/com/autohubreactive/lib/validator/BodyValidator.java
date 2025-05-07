package com.autohubreactive.lib.validator;

import com.autohubreactive.exception.AutoHubResponseStatusException;
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
public class BodyValidator<T> {

    private final Validator validator;

    public Mono<T> validateBody(T body) {
        return Mono.just(getErrors(body))
                .handle((errors, sink) -> {
                    if (ObjectUtils.isEmpty(errors) || errors.getAllErrors().isEmpty()) {
                        sink.next(body);

                        return;
                    }

                    sink.error(new AutoHubResponseStatusException(HttpStatus.BAD_REQUEST, errors.toString()));
                });
    }

    private Errors getErrors(T body) {
        Errors errors = new BeanPropertyBindingResult(body, body.getClass().getName());
        validator.validate(body, errors);

        return errors;
    }

}
