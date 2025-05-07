package com.autohubreactive.agency.validator;

import com.autohubreactive.dto.agency.BranchRequest;
import com.autohubreactive.lib.validator.BodyValidator;
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
