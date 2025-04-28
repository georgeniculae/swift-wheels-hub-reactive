package com.autohubreactive.agency.validator;

import com.autohubreactive.dto.EmployeeRequest;
import com.autohubreactive.lib.validator.BodyValidator;
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
