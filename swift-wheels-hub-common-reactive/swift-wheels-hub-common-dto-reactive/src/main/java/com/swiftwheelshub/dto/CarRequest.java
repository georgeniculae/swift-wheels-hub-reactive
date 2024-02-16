package com.swiftwheelshub.dto;

import lombok.Builder;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;

@Builder
public record CarRequest(
        String id,

        @NonNull
        String make,

        @NonNull
        String model,

        BodyCategory bodyCategory,

        int yearOfProduction,

        String color,

        int mileage,

        CarState carState,

        BigDecimal amount,

        String originalBranchId,

        String actualBranchId,

        String urlOfImage
) {

    @Override
    public String toString() {
        return "CarRequest{" + "\n" +
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
                "urlOfImage='" + urlOfImage + "\n" +
                "}";
    }

}
