package com.autohubreactive.dto.booking;

import lombok.Builder;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record CreatedBookingReprocessRequest(
        String id,

        @NonNull
        LocalDate dateOfBooking,

        BookingState status,

        String customerUsername,

        String customerEmail,

        @NonNull
        String actualCarId,

        String previousCarId,

        @NonNull
        LocalDate dateFrom,

        @NonNull
        LocalDate dateTo,

        BigDecimal rentalCarPrice,

        String rentalBranchId,

        String returnBranchId
) {
}
