package com.swiftwheelshubreactive.requestvalidator.handler;

import com.swiftwheelshubreactive.dto.IncomingRequestDetails;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshubreactive.requestvalidator.service.RedisService;
import com.swiftwheelshubreactive.requestvalidator.service.SwaggerRequestValidatorService;
import com.swiftwheelshubreactive.requestvalidator.validator.IncomingRequestDetailsValidator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RequestValidatorHandler {

    private final SwaggerRequestValidatorService swaggerRequestValidatorService;
    private final RedisService redisService;
    private final IncomingRequestDetailsValidator incomingRequestDetailsValidator;

    public Mono<ServerResponse> validateRequest(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(IncomingRequestDetails.class)
                .flatMap(incomingRequestDetailsValidator::validateBody)
                .flatMap(swaggerRequestValidatorService::validateRequest)
                .flatMap(requestValidationReport -> ServerResponse.ok().bodyValue(requestValidationReport));
    }

    public Mono<ServerResponse> repopulateRedisWithSwaggerFiles(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(String.class)
                .filter(ObjectUtils::isNotEmpty)
                .switchIfEmpty(Mono.error(new SwiftWheelsHubResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is empty")))
                .flatMap(redisService::repopulateRedisWithSwaggerFiles)
                .flatMap(requestValidationReport -> ServerResponse.ok().bodyValue(requestValidationReport));
    }

}
