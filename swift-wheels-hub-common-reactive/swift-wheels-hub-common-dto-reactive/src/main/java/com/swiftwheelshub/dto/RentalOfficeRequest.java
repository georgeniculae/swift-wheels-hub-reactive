package com.swiftwheelshub.dto;

import lombok.Builder;
import org.springframework.lang.NonNull;

@Builder
public record RentalOfficeRequest(
        String id,

        @NonNull
        String name,

        @NonNull
        String contactAddress,

        String logoType
) {

    @Override
    public String toString() {
        return "RentalOfficeRequest{" + "\n" +
                "id=" + id + "\n" +
                "name='" + name + "\n" +
                "contactAddress='" + contactAddress + "\n" +
                "logoType='" + logoType + "\n" +
                "}";
    }

}