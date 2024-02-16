package com.swiftwheelshub.dto;

import lombok.Builder;
import org.springframework.lang.NonNull;

import java.util.List;

@Builder
public record AuditLogInfoRequest(
        @NonNull
        String methodName,
        String username,
        List<String> parametersValues
) {

    @Override
    public String toString() {
        return "AuditLogInfoRequest{" + "\n" +
                "methodName=" + methodName + "\n" +
                "username=" + username + "\n" +
                "parametersValues=" + parametersValues + "\n" +
                "}";
    }

}
