package com.swiftwheelshubreactive.dto;

import lombok.Builder;

@Builder
public record BookingClosingDetails(
        String bookingId,
        String receptionistEmployeeId,
        CarState carState
) {

    @Override
    public String toString() {
        return "BookingClosingDetails{" + "\n" +
                "bookingId=" + bookingId + "\n" +
                "receptionistEmployeeId=" + receptionistEmployeeId + "\n" +
                "carState=" + carState + "\n" +
                "}";
    }

}
