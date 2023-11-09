package com.carrental.lib.util;

import lombok.experimental.UtilityClass;
import org.springframework.web.reactive.function.server.ServerRequest;

@UtilityClass
public class ServerRequestUtil {

    private static final String AUTHORIZATION = "Authorization";
    private static final String X_API_KEY = "X-API-KEY";
    private static final String X_USERNAME = "X-USERNAME";

    public static String getPathVariable(ServerRequest serverRequest, String name) {
        return serverRequest.pathVariable(name);
    }

    public static String getQueryParam(ServerRequest serverRequest, String name) {
        return serverRequest.queryParams().getFirst(name);
    }

    public static String getAuthorizationHeader(ServerRequest serverRequest) {
        return serverRequest.headers().firstHeader(AUTHORIZATION);
    }

    public static String getApiKeyHeader(ServerRequest serverRequest) {
        return serverRequest.headers().firstHeader(X_API_KEY);
    }

    public static String getUsername(ServerRequest serverRequest) {
        return serverRequest.headers().firstHeader(X_USERNAME);
    }

}
