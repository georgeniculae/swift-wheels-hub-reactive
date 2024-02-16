package com.swiftwheelshub.dto;

import lombok.Builder;

@Builder
public record BookingClosingDetails(
        String bookingId,
        Long receptionistEmployeeId,
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
