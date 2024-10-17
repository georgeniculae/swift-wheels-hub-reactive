package com.swiftwheelshubreactive.dto;

import lombok.Builder;

@Builder
public record StatusUpdateResponse(boolean isUpdateSuccessful) {
}
