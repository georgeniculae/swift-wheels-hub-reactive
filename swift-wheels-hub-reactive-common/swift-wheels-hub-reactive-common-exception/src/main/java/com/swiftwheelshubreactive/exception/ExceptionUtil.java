package com.swiftwheelshubreactive.exception;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionUtil {

    public static RuntimeException getException(Throwable e) {
        if (e instanceof SwiftWheelsHubNotFoundException swiftWheelsHubNotFoundException) {
            return swiftWheelsHubNotFoundException;
        }

        if (e instanceof SwiftWheelsHubResponseStatusException swiftWheelsHubResponseStatusException) {
            return swiftWheelsHubResponseStatusException;
        }

        return new SwiftWheelsHubException(e);
    }

}
