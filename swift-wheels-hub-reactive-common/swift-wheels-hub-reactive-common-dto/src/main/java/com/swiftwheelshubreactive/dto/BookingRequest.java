package com.swiftwheelshubreactive.dto;

import lombok.Builder;
import org.springframework.lang.NonNull;

import java.time.LocalDate;

@Builder
public record BookingRequest(
        @NonNull
        LocalDate dateOfBooking,

        @NonNull
        String carId,

        @NonNull
        LocalDate dateFrom,

        @NonNull
        LocalDate dateTo,

        @NonNull
        String rentalBranchId
) {

    @Override
    public String toString() {
        return "BookingRequest{" + "\n" +
                "dateOfBooking=" + dateOfBooking + "\n" +
                "carId=" + carId + "\n" +
                "dateFrom=" + dateFrom + "\n" +
                "dateTo=" + dateTo + "\n" +
                "rentalBranchId=" + rentalBranchId + "\n" +
                "}";
    }

}
