package com.swiftwheelshubreactive.dto;

import lombok.Builder;
import org.springframework.lang.NonNull;

@Builder
public record RentalOfficeRequest(
        @NonNull
        String name,

        @NonNull
        String contactAddress,

        @NonNull
        String phoneNumber
) {

    @Override
    public String toString() {
        return "RentalOfficeRequest{" + "\n" +
                "name='" + name + "\n" +
                "contactAddress='" + contactAddress + "\n" +
                "phoneNumber='" + phoneNumber + "\n" +
                "}";
    }

}
