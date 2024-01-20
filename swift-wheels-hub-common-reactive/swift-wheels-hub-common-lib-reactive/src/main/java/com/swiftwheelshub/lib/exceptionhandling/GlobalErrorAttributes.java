package com.swiftwheelshub.lib.exceptionhandling;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    public static final String UNEXPECTED_ERROR = "Unexpected error";
    public static final String STATUS = "status";
    public static final String MESSAGE = "message";

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(request, options);

        return addErrorAttributes(request, errorAttributes);
    }

    private Map<String, Object> addErrorAttributes(ServerRequest request, Map<String, Object> errorAttributes) {
        Throwable error = getError(request);
        String message = getMessage(errorAttributes, error);

        errorAttributes.put(MESSAGE, message);

        return errorAttributes;
    }

    private String getMessage(Map<String, Object> errorAttributes, Throwable error) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.value() == (Integer) errorAttributes.get(STATUS)) {
            return UNEXPECTED_ERROR;
        }

        return ((ResponseStatusException) error).getReason();
    }

}
