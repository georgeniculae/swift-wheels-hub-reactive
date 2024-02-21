package com.swiftwheelshubreactive.requestvalidator.handler;

import com.swiftwheelshubreactive.dto.IncomingRequestDetails;
import com.swiftwheelshubreactive.requestvalidator.service.RedisService;
import com.swiftwheelshubreactive.requestvalidator.service.SwaggerRequestValidatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RequestValidatorHandler {

    private final SwaggerRequestValidatorService swaggerRequestValidatorService;
    private final RedisService redisService;

    public Mono<ServerResponse> validateRequest(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(IncomingRequestDetails.class)
                .flatMap(swaggerRequestValidatorService::validateRequest)
                .flatMap(requestValidationReport -> ServerResponse.ok().bodyValue(requestValidationReport));
    }

    public Mono<ServerResponse> repopulateRedisWithSwaggerFiles(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(String.class)
                .flatMap(redisService::repopulateRedisWithSwaggerFiles)
                .flatMap(requestValidationReport -> ServerResponse.ok().bodyValue(requestValidationReport));
    }

}
