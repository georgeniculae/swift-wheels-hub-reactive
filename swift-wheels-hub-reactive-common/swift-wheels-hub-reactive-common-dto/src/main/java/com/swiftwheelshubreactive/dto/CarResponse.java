package com.swiftwheelshubreactive.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CarResponse(
        String id,
        String make,
        String model,
        BodyCategory bodyCategory,
        Integer yearOfProduction,
        String color,
        Integer mileage,
        CarState carState,
        BigDecimal amount,
        String originalBranchId,
        String actualBranchId
) {

    @Override
    public String toString() {
        return "CarResponse{" + "\n" +
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
