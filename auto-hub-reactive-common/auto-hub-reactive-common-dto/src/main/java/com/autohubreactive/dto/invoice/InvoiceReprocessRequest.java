package com.autohubreactive.dto.invoice;

import lombok.Builder;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record InvoiceReprocessRequest(
        @NonNull
        String invoiceId,

        @NonNull
        String carId,

        String receptionistEmployeeId,

        @NonNull
        String returnBranchId,

        @NonNull
        String bookingId,

        LocalDate carReturnDate,

        @NonNull
        Boolean isVehicleDamaged,

        BigDecimal damageCost,

        BigDecimal additionalPayment,

        BigDecimal totalAmount,

        String comments
) {

    @Override
    public String toString() {
        return "InvoiceReprocessRequest{" +
                "invoiceId='" + invoiceId + '\'' +
                ", carId='" + carId + '\'' +
                ", receptionistEmployeeId='" + receptionistEmployeeId + '\'' +
                ", returnBranchId='" + returnBranchId + '\'' +
                ", bookingId='" + bookingId + '\'' +
                ", carReturnDate=" + carReturnDate +
                ", isVehicleDamaged=" + isVehicleDamaged +
                ", damageCost=" + damageCost +
                ", additionalPayment=" + additionalPayment +
                ", totalAmount=" + totalAmount +
                ", comments='" + comments + '\'' +
                '}';
    }

}
