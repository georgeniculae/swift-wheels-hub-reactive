package com.swiftwheelshubreactive.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CarState {

    AVAILABLE("Available"),
    NOT_AVAILABLE("Not available"),
    BROKEN("Broken"),
    IN_REPAIR("In repair"),
    IN_SERVICE("In service");

    private final String displayName;

}
