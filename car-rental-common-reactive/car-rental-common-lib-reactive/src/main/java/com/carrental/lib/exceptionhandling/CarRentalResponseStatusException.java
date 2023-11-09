package com.carrental.lib.exceptionhandling;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class CarRentalResponseStatusException extends ResponseStatusException {

    public CarRentalResponseStatusException(HttpStatusCode status, String reason) {
        super(status, reason);
    }

}
