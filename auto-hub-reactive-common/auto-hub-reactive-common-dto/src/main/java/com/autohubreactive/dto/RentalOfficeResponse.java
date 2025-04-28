package com.autohubreactive.dto;

import lombok.Builder;
import org.springframework.lang.NonNull;

@Builder
public record RentalOfficeResponse(
        String id,

        @NonNull
        String name,

        @NonNull
        String contactAddress,

        @NonNull
        String phoneNumber
) {

    @Override
    public String toString() {
        return "RentalOfficeResponse{" + "\n" +
                "id=" + id + "\n" +
                "name=" + name + "\n" +
                "contactAddress=" + contactAddress + "\n" +
                "phoneNumber=" + phoneNumber + "\n" +
                "}";
    }

}
