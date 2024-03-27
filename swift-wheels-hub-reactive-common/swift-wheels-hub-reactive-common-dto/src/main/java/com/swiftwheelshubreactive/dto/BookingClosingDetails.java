package com.swiftwheelshubreactive.dto;

import lombok.Builder;
import org.springframework.lang.NonNull;

@Builder
public record BookingClosingDetails(
        @NonNull
        String bookingId,

        @NonNull
        String receptionistEmployeeId,

        @NonNull
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
