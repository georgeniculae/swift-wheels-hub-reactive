package com.swiftwheelshubreactive.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class SwiftWheelsHubNotFoundException extends ResponseStatusException {

    public SwiftWheelsHubNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

}
