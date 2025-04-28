package com.autohubreactive.dto;

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

        String comments,

        InvoiceProcessState invoiceProcessState
) {

    @Override
    public String toString() {
        return "InvoiceResponse{" +
                "id='" + id + '\'' +
                ", customerUsername='" + customerUsername + '\'' +
                ", customerEmail='" + customerEmail + '\'' +
                ", carId='" + carId + '\'' +
                ", receptionistEmployeeId='" + receptionistEmployeeId + '\'' +
                ", returnBranchId='" + returnBranchId + '\'' +
                ", bookingId='" + bookingId + '\'' +
                ", carReturnDate=" + carReturnDate +
                ", dateTo=" + dateTo +
                ", dateFrom=" + dateFrom +
                ", isVehicleDamaged=" + isVehicleDamaged +
                ", damageCost=" + damageCost +
                ", additionalPayment=" + additionalPayment +
                ", totalAmount=" + totalAmount +
                ", rentalCarPrice=" + rentalCarPrice +
                ", comments='" + comments + '\'' +
                ", invoiceProcessState=" + invoiceProcessState +
                '}';
    }

}
