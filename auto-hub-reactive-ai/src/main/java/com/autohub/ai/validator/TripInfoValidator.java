package com.autohub.ai.validator;

import com.autohubreactive.dto.TripInfo;
import com.autohubreactive.lib.validator.BodyValidator;
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
