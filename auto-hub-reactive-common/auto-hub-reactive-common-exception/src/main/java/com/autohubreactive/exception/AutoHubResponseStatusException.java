package com.autohubreactive.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class AutoHubResponseStatusException extends ResponseStatusException {

    public AutoHubResponseStatusException(HttpStatusCode status, String reason) {
        super(status, reason);
    }

}
