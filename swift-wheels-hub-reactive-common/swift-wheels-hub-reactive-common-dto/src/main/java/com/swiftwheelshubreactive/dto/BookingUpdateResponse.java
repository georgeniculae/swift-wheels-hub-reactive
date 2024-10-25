package com.swiftwheelshubreactive.dto;

import lombok.Builder;

@Builder
public record BookingUpdateResponse(boolean isSuccessful) {
}
