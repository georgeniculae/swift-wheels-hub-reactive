package com.autohubreactive.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AvailableCarInfo(
        String id,
        String actualBranchId,
        BigDecimal amount
) {
}
