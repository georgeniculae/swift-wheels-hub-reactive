package com.autohubreactive.dto;

import lombok.Builder;

@Builder
public record BookingUpdateResponse(boolean isSuccessful) {
}
