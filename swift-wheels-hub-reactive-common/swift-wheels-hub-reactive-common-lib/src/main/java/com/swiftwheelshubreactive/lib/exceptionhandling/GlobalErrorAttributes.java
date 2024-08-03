package com.swiftwheelshubreactive.lib.exceptionhandling;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    private static final String UNEXPECTED_ERROR = "Unexpected error";
    private static final String STATUS = "status";
    private static final String MESSAGE = "message";

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(request, options);

        Throwable error = super.getError(request);
        String message = getMessage(errorAttributes, error);

        errorAttributes.put(MESSAGE, message);

        return errorAttributes;
    }

    private String getMessage(Map<String, Object> errorAttributes, Throwable error) {
        String message = error.getMessage();

        if (HttpStatus.INTERNAL_SERVER_ERROR.value() == (Integer) errorAttributes.get(STATUS) && message.length() > 500) {
            return UNEXPECTED_ERROR;
        }

        if (error instanceof ResponseStatusException responseStatusException) {
            return responseStatusException.getReason();
        }

        return message;
    }

}
