package com.swiftwheelshubreactive.dto;

import lombok.Builder;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
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

        BigDecimal amount,

        BigDecimal rentalCarPrice,

        @NonNull
        String rentalBranchId,

        String returnBranchId
) {

    @Override
    public String toString() {
        return "BookingRequest{" + "\n" +
                "dateOfBooking=" + dateOfBooking + "\n" +
                "previousCarId=" + carId + "\n" +
                "dateFrom=" + dateFrom + "\n" +
                "dateTo=" + dateTo + "\n" +
                "amount=" + amount + "\n" +
                "rentalCarPrice=" + rentalCarPrice + "\n" +
                "rentalBranchId=" + rentalBranchId + "\n" +
                "returnBranchId=" + returnBranchId + "\n" +
                "}";
    }

}
