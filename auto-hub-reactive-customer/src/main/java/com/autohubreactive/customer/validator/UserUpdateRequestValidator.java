package com.autohubreactive.customer.validator;

import com.autohubreactive.dto.customer.UserUpdateRequest;
import com.autohubreactive.lib.validator.BodyValidator;
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
