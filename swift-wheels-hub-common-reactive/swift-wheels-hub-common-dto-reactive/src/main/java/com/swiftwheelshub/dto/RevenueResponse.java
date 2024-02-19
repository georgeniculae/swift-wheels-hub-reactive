package com.swiftwheelshub.dto;

import lombok.Builder;
import org.springframework.lang.NonNull;

import java.time.LocalDate;

@Builder
public record RevenueResponse(
        String id,

        @NonNull
        LocalDate dateOfRevenue,

        @NonNull
        Double amountFromBooking
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
