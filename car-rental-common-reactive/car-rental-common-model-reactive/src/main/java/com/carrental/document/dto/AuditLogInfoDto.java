package com.carrental.document.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AuditLogInfoDto(@NotEmpty(message = "Method name cannot be empty")
                              String methodName,
                              String username,
                              List<String> parametersValues) {

    @Override
    public String toString() {
        return "AuditLogInfoDto{" + "\n" +
                "methodName=" + methodName + "\n" +
                "username=" + username + "\n" +
                "parametersValues=" + parametersValues + "\n" +
                "}";
    }

}
