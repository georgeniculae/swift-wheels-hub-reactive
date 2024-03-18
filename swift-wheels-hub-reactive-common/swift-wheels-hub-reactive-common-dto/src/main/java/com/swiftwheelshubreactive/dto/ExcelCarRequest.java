package com.swiftwheelshubreactive.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;

@Builder
public record ExcelCarRequest(
        @NonNull
        @Size(min = 2)
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
        String actualBranchId,

        byte[] image
) {

    @Override
    public String toString() {
        return "ExcelCarRequest{" + "\n" +
                "make='" + make + "\n" +
                "model='" + model + "\n" +
                "bodyCategory=" + bodyCategory + "\n" +
                "yearOfProduction=" + yearOfProduction + "\n" +
                "color='" + color + "\n" +
                "mileage=" + mileage + "\n" +
                "carState=" + carState + "\n" +
                "amount=" + amount + "\n" +
                "originalBranchId=" + originalBranchId + "\n" +
                "actualBranchId=" + actualBranchId + "\n" +
                "}";
    }

}
