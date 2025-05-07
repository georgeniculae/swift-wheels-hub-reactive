package com.autohubreactive.lib.exceptionhandling;

import com.autohubreactive.exception.AutoHubException;
import com.autohubreactive.exception.AutoHubNotFoundException;
import com.autohubreactive.exception.AutoHubResponseStatusException;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@UtilityClass
public class ExceptionUtil {

    public static RuntimeException handleException(Throwable e) {
        if (e instanceof WebClientResponseException webClientResponseException) {
            return new AutoHubResponseStatusException(
                    webClientResponseException.getStatusCode(),
                    webClientResponseException.getResponseBodyAsString()
            );
        }

        if (e instanceof AutoHubNotFoundException autoHubNotFoundException) {
            return autoHubNotFoundException;
        }

        if (e instanceof AutoHubResponseStatusException autoHubResponseStatusException) {
            return autoHubResponseStatusException;
        }

        if (e instanceof AutoHubException autoHubException) {
            return autoHubException;
        }

        return new AutoHubException(e.getMessage());
    }

    public static HttpStatusCode extractExceptionStatusCode(Throwable e) {
        if (e instanceof WebClientResponseException webClientResponseException) {
            return webClientResponseException.getStatusCode();
        }

        if (e instanceof AutoHubNotFoundException autoHubNotFoundException) {
            return autoHubNotFoundException.getStatusCode();
        }

        if (e instanceof AutoHubResponseStatusException autoHubResponseStatusException) {
            return autoHubResponseStatusException.getStatusCode();
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

}
