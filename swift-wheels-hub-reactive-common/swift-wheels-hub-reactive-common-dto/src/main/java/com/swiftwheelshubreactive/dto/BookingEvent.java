package com.swiftwheelshubreactive.dto;

import lombok.Builder;

@Builder
public record BookingEvent(
        String bookingId,
        SagaEvent sagaEvent
) {
}
