package com.autohubreactive.dto;

import lombok.Builder;
import org.springframework.lang.NonNull;

@Builder
public record EmployeeRequest(
        @NonNull
        String firstName,

        @NonNull
        String lastName,

        @NonNull
        String jobPosition,

        @NonNull
        String workingBranchId
) {

    @Override
    public String toString() {
        return "EmployeeRequest{" + "\n" +
                "firstName=" + firstName + "\n" +
                "lastName=" + lastName + "\n" +
                "jobPosition=" + jobPosition + "\n" +
                "workingBranchId=" + workingBranchId + "\n" +
                "}";
    }

}
