package com.swiftwheelshubreactive.lib.exceptionhandling;

import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubResponseStatusException;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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

        if (e instanceof SwiftWheelsHubException swiftWheelsHubException) {
            return swiftWheelsHubException;
        }

        return new SwiftWheelsHubException(e.getMessage());
    }

    public static HttpStatusCode extractExceptionStatusCode(Throwable e) {
        if (e instanceof WebClientResponseException webClientResponseException) {
            return webClientResponseException.getStatusCode();
        }

        if (e instanceof SwiftWheelsHubNotFoundException swiftWheelsHubNotFoundException) {
            return swiftWheelsHubNotFoundException.getStatusCode();
        }

        if (e instanceof SwiftWheelsHubResponseStatusException swiftWheelsHubResponseStatusException) {
            return swiftWheelsHubResponseStatusException.getStatusCode();
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

}
