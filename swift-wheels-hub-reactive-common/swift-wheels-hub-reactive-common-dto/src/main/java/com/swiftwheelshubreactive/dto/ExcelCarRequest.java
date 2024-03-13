package com.swiftwheelshubreactive.dto;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;

@Builder
public record ExcelCarRequest(

        String id,

        @NonNull
        @Min(2)
        String make,

        @NonNull
        String model,

        @NonNull
        BodyCategory bodyCategory,

        int yearOfProduction,

        @NonNull
        String color,

        int mileage,

        @NonNull
        CarState carState,

        @NonNull
        BigDecimal amount,

        String originalBranchId,

        String actualBranchId,

        byte[] image
) {

    @Override
    public String toString() {
        return "ExcelCarRequest{" + "\n" +
                "id=" + id +
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
