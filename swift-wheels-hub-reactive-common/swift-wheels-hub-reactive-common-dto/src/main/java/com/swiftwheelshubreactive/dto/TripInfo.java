package com.swiftwheelshubreactive.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.NonNull;

import java.time.LocalDate;

@Builder(toBuilder = true)
public record TripInfo(
        @NotEmpty
        String destination,

        @NonNull
        Integer peopleCount,

        @NotEmpty
        String tripKind,

        @NotNull
        LocalDate tripDate
) {
}
