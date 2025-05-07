package com.autohubreactive.dto.common;

import lombok.Builder;

@Builder
public record RequestValidationReport(String errorMessage) {

    @Override
    public String toString() {
        return "RequestValidationReport{" + "\n" +
                "errorMessage=" + errorMessage + "\n" +
                "}";
    }

}
