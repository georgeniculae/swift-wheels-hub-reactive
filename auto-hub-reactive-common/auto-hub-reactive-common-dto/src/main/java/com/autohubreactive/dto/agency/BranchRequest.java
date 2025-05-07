package com.autohubreactive.dto.agency;

import lombok.Builder;
import org.springframework.lang.NonNull;

@Builder
public record BranchRequest(
        @NonNull
        String name,

        @NonNull
        String address,

        @NonNull
        String rentalOfficeId
) {

    @Override
    public String toString() {
        return "BranchRequest{" + "\n" +
                ", name=" + name + "\n" +
                ", address=" + address + "\n" +
                ", rentalOfficeId=" + rentalOfficeId + "\n" +
                "}";
    }

}
