package com.swiftwheelshub.dto;

import lombok.Builder;

@Builder
public record RequestValidationReport(String errorMessage) {
}
