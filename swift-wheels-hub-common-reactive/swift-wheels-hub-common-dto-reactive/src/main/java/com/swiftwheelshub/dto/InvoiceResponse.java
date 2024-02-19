package com.swiftwheelshub.dto;

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

        @NonNull
        String bookingId,

        LocalDate carDateOfReturn,

        Boolean isVehicleDamaged,

        BigDecimal damageCost,

        BigDecimal additionalPayment,

        BigDecimal totalAmount,

        String comments
) {

    @Override
    public String toString() {
        return "InvoiceResponse{" + "\n" +
                "id=" + id + "\n" +
                "customerUsername='" + customerUsername + "\n" +
                "customerEmail='" + customerEmail + "\n" +
                "carId=" + carId + "\n" +
                "receptionistEmployeeId=" + receptionistEmployeeId + "\n" +
                "bookingId=" + bookingId + "\n" +
                "carDateOfReturn=" + carDateOfReturn + "\n" +
                "isVehicleDamaged=" + isVehicleDamaged + "\n" +
                "damageCost=" + damageCost + "\n" +
                "additionalPayment=" + additionalPayment + "\n" +
                "totalAmount=" + totalAmount + "\n" +
                "comments='" + comments + "\n" +
                "}";
    }

}
