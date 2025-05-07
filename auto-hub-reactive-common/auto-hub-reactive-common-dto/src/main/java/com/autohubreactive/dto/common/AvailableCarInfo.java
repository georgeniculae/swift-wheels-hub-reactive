package com.autohubreactive.dto.common;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AvailableCarInfo(
        String id,
        String actualBranchId,
        BigDecimal amount
) {
}
