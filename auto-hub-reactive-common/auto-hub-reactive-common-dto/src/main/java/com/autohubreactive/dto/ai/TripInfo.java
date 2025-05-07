package com.autohubreactive.dto.ai;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder(toBuilder = true)
public record TripInfo(
        @NotEmpty
        String destination,

        @NotNull
        Integer peopleCount,

        @NotEmpty
        String tripKind,

        @NotNull
        LocalDate tripDate
) {
}
