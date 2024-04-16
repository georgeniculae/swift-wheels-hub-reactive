package com.swiftwheelshubreactive.agency.validator;

import com.swiftwheelshubreactive.dto.EmployeeRequest;
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
public class EmployeeRequestValidator {

    private final Validator validator;

    public final Mono<EmployeeRequest> validateBody(EmployeeRequest employeeRequest) {
        return Mono.just(getErrors(employeeRequest))
                .map(errors -> {
                    if (ObjectUtils.isEmpty(errors) || errors.getAllErrors().isEmpty()) {
                        return employeeRequest;
                    }

                    throw new SwiftWheelsHubResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            errors.toString()
                    );
                });
    }

    private Errors getErrors(EmployeeRequest employeeRequest) {
        Errors errors = new BeanPropertyBindingResult(employeeRequest, employeeRequest.getClass().getName());
        validator.validate(employeeRequest, errors);

        return errors;
    }

}
