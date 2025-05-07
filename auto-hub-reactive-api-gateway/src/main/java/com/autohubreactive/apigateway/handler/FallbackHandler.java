package com.autohubreactive.apigateway.handler;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class FallbackHandler {

    private static final String MESSAGE = "Unavailable service. Try again later";

    public Mono<ServerResponse> fallback(ServerRequest serverRequest) {
        return Mono.just(MESSAGE)
                .flatMap(message -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue(message));
    }

}
