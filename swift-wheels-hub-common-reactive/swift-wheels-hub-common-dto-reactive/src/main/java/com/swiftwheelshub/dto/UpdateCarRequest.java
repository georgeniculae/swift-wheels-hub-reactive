package com.swiftwheelshub.dto;

import lombok.Builder;

@Builder
public record UpdateCarRequest(
        String carId,
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
