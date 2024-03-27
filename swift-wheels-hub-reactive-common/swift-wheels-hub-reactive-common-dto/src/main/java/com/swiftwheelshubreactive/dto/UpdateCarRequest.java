package com.swiftwheelshubreactive.dto;

import lombok.Builder;
import org.springframework.lang.NonNull;

@Builder
public record UpdateCarRequest(
        @NonNull
        String carId,

        @NonNull
        CarState carState
) {

    @Override
    public String toString() {
        return "UpdateCarRequest{" + "\n" +
                "carId=" + carId + "\n" +
                "carState=" + carState + "\n" +
                "}";
    }

}
