package com.swiftwheelshubreactive.dto;

import lombok.Builder;
import org.springframework.lang.NonNull;

@Builder
public record EmployeeRequest(
        String id,

        @NonNull
        String firstName,

        @NonNull
        String lastName,

        @NonNull
        String jobPosition,

        String workingBranchId
) {

    @Override
    public String toString() {
        return "EmployeeRequest{" + "\n" +
                "id=" + id + "\n" +
                "firstName='" + firstName + "\n" +
                "lastName='" + lastName + "\n" +
                "jobPosition='" + jobPosition + "\n" +
                "workingBranchId=" + workingBranchId + "\n" +
                "}";
    }

}
