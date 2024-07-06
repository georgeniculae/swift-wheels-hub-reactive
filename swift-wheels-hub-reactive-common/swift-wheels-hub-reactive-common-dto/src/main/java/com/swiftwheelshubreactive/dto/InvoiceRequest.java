package com.swiftwheelshubreactive.dto;

import lombok.Builder;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record InvoiceRequest(
        @NonNull
        String carId,

        String receptionistEmployeeId,

        @NonNull
        String bookingId,

        LocalDate carReturnDate,

        Boolean isVehicleDamaged,

        BigDecimal damageCost,

        BigDecimal additionalPayment,

        BigDecimal totalAmount,

        String comments
) {

    @Override
    public String toString() {
        return "InvoiceRequest{" + "\n" +
                "carId=" + carId + "\n" +
                "receptionistEmployeeId=" + receptionistEmployeeId + "\n" +
                "bookingId=" + bookingId + "\n" +
                "carReturnDate=" + carReturnDate + "\n" +
                "isVehicleDamaged=" + isVehicleDamaged + "\n" +
                "damageCost=" + damageCost + "\n" +
                "additionalPayment=" + additionalPayment + "\n" +
                "totalAmount=" + totalAmount + "\n" +
                "comments='" + comments + "\n" +
                "}";
    }

}
