package com.swiftwheelshub.ai.validator;

import com.swiftwheelshubreactive.dto.TripInfo;
import com.swiftwheelshubreactive.lib.validator.BodyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TripInfoValidator {

    private final BodyValidator<TripInfo> bodyValidator;

    public Mono<TripInfo> validateBody(TripInfo tripInfo) {
        return bodyValidator.validateBody(tripInfo);
    }

}
