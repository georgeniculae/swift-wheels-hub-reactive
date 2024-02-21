package com.swiftwheelshubreactive.dto;

import lombok.Builder;

@Builder
public record RequestValidationReport(String errorMessage) {
}
