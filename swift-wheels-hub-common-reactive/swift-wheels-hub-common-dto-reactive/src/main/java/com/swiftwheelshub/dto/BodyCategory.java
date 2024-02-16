package com.swiftwheelshub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BodyCategory {

    HATCHBACK("Hatchback"),
    SEDAN("Sedan"),
    SUV("SUV"),
    COUPE("Coupe"),
    CONVERTIBLE("Convertible"),
    WAGON("Wagon"),
    VAN("Van");

    private final String displayName;

}
