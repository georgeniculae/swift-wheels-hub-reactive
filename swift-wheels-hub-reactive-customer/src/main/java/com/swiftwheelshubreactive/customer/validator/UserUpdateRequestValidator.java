package com.swiftwheelshubreactive.customer.validator;

import com.swiftwheelshubreactive.dto.UserUpdateRequest;
import com.swiftwheelshubreactive.lib.validator.BodyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserUpdateRequestValidator {

    private final BodyValidator<UserUpdateRequest> validator;

    public Mono<UserUpdateRequest> validateBody(UserUpdateRequest userUpdateRequest) {
        return validator.validateBody(userUpdateRequest);
    }

}
