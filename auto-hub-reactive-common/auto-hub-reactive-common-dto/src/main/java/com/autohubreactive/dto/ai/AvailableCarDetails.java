package com.autohubreactive.dto.ai;

import com.autohubreactive.dto.agency.BodyCategory;
import lombok.Builder;
import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;

@Builder
public record AvailableCarDetails(
        String id,

        @NonNull
        String make,

        @NonNull
        String model,

        @NonNull
        BodyCategory bodyCategory,

        @NonNull
        Integer yearOfProduction,

        @NonNull
        String color,

        @NonNull
        Integer mileage,

        @NonNull
        BigDecimal amount,

        @NonNull
        String carLocation
) {
}