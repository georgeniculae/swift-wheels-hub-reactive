package com.swiftwheelshubreactive.lib.util;

import lombok.experimental.UtilityClass;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.List;

@UtilityClass
public class ServerRequestUtil {

    private static final String X_API_KEY = "X-API-KEY";
    private static final String X_USERNAME = "X-USERNAME";
    private static final String X_ROLES = "X-ROLES";

    public static String getPathVariable(ServerRequest serverRequest, String name) {
        return serverRequest.pathVariable(name);
    }

    public static String getQueryParam(ServerRequest serverRequest, String name) {
        return serverRequest.queryParams().getFirst(name);
    }

    public static String getApiKeyHeader(ServerRequest serverRequest) {
        return serverRequest.headers().firstHeader(X_API_KEY);
    }

    public static List<String> getRolesHeader(ServerRequest serverRequest) {
        return serverRequest.headers().header(X_ROLES);
    }

    public static List<String> getRolesHeader(ServerHttpRequest serverHttpRequest) {
        return serverHttpRequest.getHeaders().get(X_ROLES);
    }

    public static String getUsername(ServerRequest serverRequest) {
        return serverRequest.headers().firstHeader(X_USERNAME);
    }

}
