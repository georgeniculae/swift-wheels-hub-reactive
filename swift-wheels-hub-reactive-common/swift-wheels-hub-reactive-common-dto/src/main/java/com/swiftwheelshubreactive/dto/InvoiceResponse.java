package com.swiftwheelshubreactive.dto;

import lombok.Builder;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record InvoiceResponse(
        String id,

        @NonNull
        String customerUsername,

        @NonNull
        String customerEmail,

        @NonNull
        String carId,

        String receptionistEmployeeId,

        String returnBranchId,

        @NonNull
        String bookingId,

        LocalDate carReturnDate,

        LocalDate dateTo,

        LocalDate dateFrom,

        Boolean isVehicleDamaged,

        BigDecimal damageCost,

        BigDecimal additionalPayment,

        BigDecimal totalAmount,

        BigDecimal rentalCarPrice,

        String comments
) {

    @Override
    public String toString() {
        return "InvoiceResponse{" + "\n" +
                "id=" + id + "\n" +
                "customerUsername=" + customerUsername + "\n" +
                "previousCarId=" + carId + "\n" +
                "returnBranchId=" + receptionistEmployeeId + "\n" +
                "bookingId=" + bookingId + "\n" +
                "carReturnDate=" + carReturnDate + "\n" +
                "isVehicleDamaged=" + isVehicleDamaged + "\n" +
                "damageCost=" + damageCost + "\n" +
                "additionalPayment=" + additionalPayment + "\n" +
                "totalAmount=" + totalAmount + "\n" +
                "comments=" + comments + "\n" +
                "}";
    }

}
