package com.swiftwheelshubreactive.dto;

import lombok.Builder;

@Builder
public record InvoiceEvent(
        String bookingId,
        String carId,
        String invoiceId,
        SagaEvent sagaEvent
) {
}
