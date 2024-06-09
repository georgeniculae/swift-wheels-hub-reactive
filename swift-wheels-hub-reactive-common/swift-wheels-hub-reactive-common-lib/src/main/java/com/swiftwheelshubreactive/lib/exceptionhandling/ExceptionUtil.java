package com.swiftwheelshubreactive.lib.exceptionhandling;

import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubResponseStatusException;
import lombok.experimental.UtilityClass;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@UtilityClass
public class ExceptionUtil {

    public static RuntimeException handleException(Throwable e) {
        if (e instanceof WebClientResponseException webClientResponseException) {
            return new SwiftWheelsHubResponseStatusException(
                    webClientResponseException.getStatusCode(),
                    webClientResponseException.getResponseBodyAsString()
            );
        }

        if (e instanceof SwiftWheelsHubNotFoundException swiftWheelsHubNotFoundException) {
            return swiftWheelsHubNotFoundException;
        }

        if (e instanceof SwiftWheelsHubResponseStatusException swiftWheelsHubResponseStatusException) {
            return swiftWheelsHubResponseStatusException;
        }

        return new SwiftWheelsHubException(e.getMessage());
    }

}
