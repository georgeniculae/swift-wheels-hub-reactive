package com.swiftwheelshub.dto;

import lombok.Builder;
import org.springframework.lang.NonNull;

@Builder
public record BranchRequest(
        String id,

        @NonNull
        String name,

        String address,

        String rentalOfficeId
) {

    @Override
    public String toString() {
        return "BranchRequest{" + "\n" +
                "id=" + id + "\n" +
                ", name='" + name + "\n" +
                ", address='" + address + "\n" +
                ", rentalOfficeId=" + rentalOfficeId + "\n" +
                "}";
    }

}
