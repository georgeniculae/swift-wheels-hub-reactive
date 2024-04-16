package com.swiftwheelshubreactive.agency.validator;

import com.swiftwheelshubreactive.dto.BranchRequest;
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
public class BranchRequestValidator {

    private final Validator validator;

    public final Mono<BranchRequest> validateBody(BranchRequest branchRequest) {
        return Mono.just(getErrors(branchRequest))
                .map(errors -> {
                    if (ObjectUtils.isEmpty(errors) || errors.getAllErrors().isEmpty()) {
                        return branchRequest;
                    }

                    throw new SwiftWheelsHubResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            errors.toString()
                    );
                });
    }

    private Errors getErrors(BranchRequest branchRequest) {
        Errors errors = new BeanPropertyBindingResult(branchRequest, branchRequest.getClass().getName());
        validator.validate(branchRequest, errors);

        return errors;
    }

}
