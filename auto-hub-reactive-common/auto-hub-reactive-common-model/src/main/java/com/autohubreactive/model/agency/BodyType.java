package com.autohubreactive.model.agency;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BodyType {

    HATCHBACK("Hatchback"),
    SEDAN("Sedan"),
    SUV("SUV"),
    COUPE("Coupe"),
    CONVERTIBLE("Convertible"),
    WAGON("Wagon"),
    VAN("Van");

    private final String displayName;

}
