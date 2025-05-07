package com.autohubreactive.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AutoHubNotFoundException extends ResponseStatusException {

    public AutoHubNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

}
