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
        CarPhase carPhase
) {

    @Override
    public String toString() {
        return "BookingClosingDetails{" + "\n" +
                "bookingId=" + bookingId + "\n" +
                "receptionistEmployeeId=" + receptionistEmployeeId + "\n" +
                "carPhase=" + carPhase + "\n" +
                "}";
    }

}
