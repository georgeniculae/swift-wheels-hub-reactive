package com.swiftwheelshubreactive.dto;

import lombok.Builder;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record RevenueResponse(
        String id,

        @NonNull
        LocalDate dateOfRevenue,

        @NonNull
        BigDecimal amountFromBooking
) {

    @Override
    public String toString() {
        return "RevenueRequest{" + "\n" +
                "id=" + id + "\n" +
                "dateOfRevenue=" + dateOfRevenue + "\n" +
                "amountFromBooking=" + amountFromBooking + "\n" +
                "}";
    }
}
