package com.swiftwheelshubreactive.dto;

import lombok.Builder;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;

@Builder
public record CarResponse(
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
        CarState carState,

        @NonNull
        BigDecimal amount,

        @NonNull
        String originalBranchId,

        @NonNull
        String actualBranchId
) {

    @Override
    public String toString() {
        return "CarResponse{" + "\n" +
                "id=" + id +
                "make=" + make + "\n" +
                "model=" + model + "\n" +
                "bodyCategory=" + bodyCategory + "\n" +
                "yearOfProduction=" + yearOfProduction + "\n" +
                "color=" + color + "\n" +
                "mileage=" + mileage + "\n" +
                "carState=" + carState + "\n" +
                "amount=" + amount + "\n" +
                "originalBranchId=" + originalBranchId + "\n" +
                "actualBranchId=" + actualBranchId + "\n" +
                "}";
    }

}
