package com.swiftwheelshub.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class SwiftWheelsHubResponseStatusException extends ResponseStatusException {

    public SwiftWheelsHubResponseStatusException(HttpStatusCode status, String reason) {
        super(status, reason);
    }

}
