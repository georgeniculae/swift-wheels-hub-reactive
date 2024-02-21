package com.swiftwheelshubreactive.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CarFields {

    MAKE("MAKE"),
    MODEL("MODEL"),
    BODY_TYPE("BODY TYPE"),
    YEAR_OF_PRODUCTION("YEAR OF PRODUCTION"),
    COLOR("COLOR"),
    MILEAGE("MILEAGE"),
    CAR_STATUS("CAR STATUS"),
    AMOUNT("AMOUNT"),
    ORIGINAL_BRANCH("ORIGINAL BRANCH ID"),
    ACTUAL_BRANCH("ACTUAL BRANCH ID"),
    URL_OF_IMAGE("URL OF IMAGE");

    private final String excelValue;

}
