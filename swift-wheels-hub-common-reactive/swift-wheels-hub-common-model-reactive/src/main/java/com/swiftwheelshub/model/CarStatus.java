package com.swiftwheelshub.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CarStatus {

    NOT_AVAILABLE("Not available"),
    BROKEN("Broken"),
    IN_REPAIR("In repair"),
    IN_SERVICE("In service"),
    AVAILABLE("Available");

    private final String displayName;

}
