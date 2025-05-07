package com.autohubreactive.dto.common;

import com.autohubreactive.dto.agency.CarState;
import lombok.Builder;
import org.springframework.lang.NonNull;

@Builder
public record CarStatusUpdate(
        @NonNull
        String carId,

        @NonNull
        CarState carState
) {

    @Override
    public String toString() {
        return "CarStatusUpdate{" + "\n" +
                "previousCarId=" + carId + "\n" +
                "carState=" + carState + "\n" +
                "}";
    }

}
