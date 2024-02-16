package com.swiftwheelshub.dto;

import lombok.Builder;
import org.springframework.lang.NonNull;

@Builder
public record BranchResponse(
        Long id,

        @NonNull
        String name,

        String address,

        String rentalOfficeId
) {

    @Override
    public String toString() {
        return "BranchResponse{" + "\n" +
                "id=" + id + "\n" +
                ", name='" + name + "\n" +
                ", address='" + address + "\n" +
                ", rentalOfficeId=" + rentalOfficeId + "\n" +
                "}";
    }

}
