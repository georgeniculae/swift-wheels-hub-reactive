package com.swiftwheelshubreactive.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public enum BodyCategory implements Serializable {

    HATCHBACK("Hatchback"),
    SEDAN("Sedan"),
    SUV("SUV"),
    COUPE("Coupe"),
    CONVERTIBLE("Convertible"),
    WAGON("Wagon"),
    VAN("Van");

    private final String displayName;

}
