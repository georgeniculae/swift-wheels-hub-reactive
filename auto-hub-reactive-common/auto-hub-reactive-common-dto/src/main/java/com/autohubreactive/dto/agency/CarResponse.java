package com.autohubreactive.dto.agency;

import com.autohubreactive.dto.common.CarState;
import lombok.Builder;
import org.jspecify.annotations.NonNull;

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
        String initialRentalOfficeId,

        @NonNull
        String actualRentalOfficeId,

        @NonNull
        String carLocation
) {

    @Override
    @NonNull
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
                "initialRentalOfficeId=" + initialRentalOfficeId + "\n" +
                "actualRentalOfficeId=" + actualRentalOfficeId + "\n" +
                "carLocation=" + carLocation + "\n" +
                "}";
    }

}
