package com.autohubreactive.dto.invoicenotification;

import lombok.Builder;

@Builder
public record EmailResponse(
        int statusCode,
        String body
) {
}
