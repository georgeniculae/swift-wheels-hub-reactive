package com.autohubreactive.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BookingState {

    IN_PROGRESS("In progress"),
    CLOSED("Closed");

    private final String displayName;

}
