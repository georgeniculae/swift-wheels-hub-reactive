package com.autohubreactive.dto.common;

import lombok.Builder;
import org.springframework.lang.NonNull;

@Builder
public record UpdateCarsRequest(
        @NonNull
        String previousCarId,

        @NonNull
        String actualCarId
) {

    @Override
    public String toString() {
        return "UpdateCarsRequest{" + "\n" +
                "previousCarId=" + previousCarId + "\n" +
                "actualCarId=" + actualCarId + "\n" +
                "}";
    }

}
