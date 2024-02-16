package com.swiftwheelshub.dto;

import lombok.Builder;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record BookingResponse(
        String id,

        @NonNull
        LocalDate dateOfBooking,

        BookingState status,

        @NonNull
        String customerUsername,

        @NonNull
        String customerEmail,

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
        return "BookingResponse{" + "\n" +
                "id=" + id + "\n" +
                "dateOfBooking=" + dateOfBooking + "\n" +
                "status=" + status +
                "customerUsername='" + customerUsername + "\n" +
                "customerEmail='" + customerEmail + "\n" +
                "carId=" + carId + "\n" +
                "dateFrom=" + dateFrom + "\n" +
                "dateTo=" + dateTo + "\n" +
                "amount=" + amount + "\n" +
                "rentalCarPrice=" + rentalCarPrice + "\n" +
                "rentalBranchId=" + rentalBranchId + "\n" +
                "returnBranchId=" + returnBranchId + "\n" +
                "}";
    }

}
