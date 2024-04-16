package com.swiftwheelshubreactive.agency.validator;

import com.swiftwheelshubreactive.dto.EmployeeRequest;
import com.swiftwheelshubreactive.lib.validator.BodyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class EmployeeRequestValidator {

    private final BodyValidator<EmployeeRequest> validator;

    public final Mono<EmployeeRequest> validateBody(EmployeeRequest employeeRequest) {
        return validator.validateBody(employeeRequest);
    }

}
