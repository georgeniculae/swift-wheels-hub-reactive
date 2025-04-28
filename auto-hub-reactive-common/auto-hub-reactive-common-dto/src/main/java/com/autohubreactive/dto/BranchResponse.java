package com.autohubreactive.dto;

import lombok.Builder;
import org.springframework.lang.NonNull;

@Builder
public record BranchResponse(
        String id,

        @NonNull
        String name,

        @NonNull
        String address,

        @NonNull
        String rentalOfficeId
) {

    @Override
    public String toString() {
        return "BranchResponse{" + "\n" +
                "id=" + id + "\n" +
                ", name=" + name + "\n" +
                ", address=" + address + "\n" +
                ", rentalOfficeId=" + rentalOfficeId + "\n" +
                "}";
    }

}
