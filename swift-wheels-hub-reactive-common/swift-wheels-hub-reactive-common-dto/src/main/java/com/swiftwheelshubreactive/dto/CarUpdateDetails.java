package com.swiftwheelshubreactive.dto;

import lombok.Builder;

@Builder
public record CarUpdateDetails(
        String carId,
        CarState carState,
        String receptionistEmployeeId
) {

    @Override
    public String toString() {
        return "CarUpdateDetails{" + "\n" +
                "carId=" + carId + "\n" +
                "carState=" + carState + "\n" +
                "receptionistEmployeeId=" + receptionistEmployeeId + "\n" +
                "}";
    }

}
