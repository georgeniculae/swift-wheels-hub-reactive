package com.swiftwheelshubreactive.dto;

import lombok.Builder;

@Builder
public record CarUpdateEvent(
        String carId,
        SagaEvent sagaEvent
) {
}
