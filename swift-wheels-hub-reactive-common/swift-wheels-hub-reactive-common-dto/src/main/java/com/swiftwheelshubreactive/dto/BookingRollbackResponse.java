package com.swiftwheelshubreactive.dto;

import lombok.Builder;

@Builder
public record BookingRollbackResponse(boolean isSuccessful, String bookingId) {
}
