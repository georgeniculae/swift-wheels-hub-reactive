package com.autohubreactive.dto.emailnotification;

import lombok.Builder;

@Builder
public record EmailResponse(
        int statusCode,
        String body
) {
}
