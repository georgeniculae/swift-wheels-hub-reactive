package com.swiftwheelshubreactive.agency.validator;

import com.swiftwheelshubreactive.dto.BranchRequest;
import com.swiftwheelshubreactive.lib.validator.BodyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BranchRequestValidator {

    private final BodyValidator<BranchRequest> validator;

    public final Mono<BranchRequest> validateBody(BranchRequest branchRequest) {
        return validator.validateBody(branchRequest);
    }

}
