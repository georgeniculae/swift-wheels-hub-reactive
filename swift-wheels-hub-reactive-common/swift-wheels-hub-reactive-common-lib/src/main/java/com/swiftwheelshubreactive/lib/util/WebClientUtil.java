package com.swiftwheelshubreactive.lib.util;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.function.Consumer;

@UtilityClass
public class WebClientUtil {

    private static final String X_API_KEY = "X-API-KEY";
    private static final String X_ROLES = "X-ROLES";

    public static Consumer<HttpHeaders> setHttpHeaders(String apiKey, List<String> roles) {
        return httpHeaders -> {
            httpHeaders.add(X_API_KEY, apiKey);
            httpHeaders.addAll(X_ROLES, roles);
        };
    }

}
