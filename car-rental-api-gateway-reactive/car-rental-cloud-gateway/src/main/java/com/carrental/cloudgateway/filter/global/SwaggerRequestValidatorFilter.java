package com.carrental.cloudgateway.filter.global;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.model.SimpleRequest;
import com.atlassian.oai.validator.report.ValidationReport;
import com.atlassian.oai.validator.whitelist.ValidationErrorsWhitelist;
import com.atlassian.oai.validator.whitelist.rule.WhitelistRules;
import com.carrental.cloudgateway.model.SwaggerFolder;
import com.carrental.lib.exceptionhandling.CarRentalException;
import com.carrental.lib.exceptionhandling.CarRentalResponseStatusException;
import io.swagger.v3.oas.models.OpenAPI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class SwaggerRequestValidatorFilter implements GlobalFilter, Ordered {

    private static final String SEPARATOR = "/";
    public static final String SWAGGER = "swagger";
    private static final String V3 = "v3";
    private static final String SWAGGER_PATH = "Swagger path";
    private static final String SWAGGER_MESSAGE = "Swagger message";
    private static final String V3_PATH = "v3 path";
    private static final String V3_MESSAGE = "v3 message";
    private final ReactiveRedisOperations<String, SwaggerFolder> redisSwagger;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return getRequestBodyAsString(exchange.getRequest())
                .map(bodyAsString -> getSimpleRequest(exchange, bodyAsString))
                .flatMap(simpleRequest -> getValidationReport(exchange, simpleRequest))
                .flatMap(validationReport -> forwardRequest(exchange, chain, validationReport))
                .onErrorResume(e -> {
                    log.error("Error while processing request: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e));
                });
    }

    @Override
    public int getOrder() {
        return 1;
    }

    private Mono<String> getRequestBodyAsString(ServerHttpRequest request) {
        return request.getBody()
                .map(dataBuffer -> dataBuffer.toString(Charset.defaultCharset()))
                .reduce(StringUtils.EMPTY, (current, next) -> current + next);
    }

    private SimpleRequest getSimpleRequest(ServerWebExchange exchange, String bodyAsString) {
        ServerHttpRequest request = exchange.getRequest();

        SimpleRequest.Builder simpleRequestBuilder =
                new SimpleRequest.Builder(request.getMethod().name(), request.getPath().value());

        request.getHeaders()
                .toSingleValueMap()
                .forEach(simpleRequestBuilder::withHeader);

        request.getQueryParams()
                .toSingleValueMap()
                .forEach(simpleRequestBuilder::withQueryParam);

        simpleRequestBuilder.withBody(bodyAsString);

        return simpleRequestBuilder.build();
    }

    private OpenAPI getSwaggerFile(ServerWebExchange exchange, Map<String, OpenAPI> swaggerIdentifierAndContent) {
        String path = exchange.getRequest().getPath().value().replaceFirst(SEPARATOR, StringUtils.EMPTY);

        return swaggerIdentifierAndContent.entrySet()
                .stream()
                .filter(entry -> path.contains(entry.getKey()))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseThrow(() -> new CarRentalException("There is no swagger file that contains path: " + path));
    }

    private Mono<ValidationReport> getValidationReport(ServerWebExchange exchange, SimpleRequest simpleRequest) {
        return redisSwagger.opsForValue()
                .get(SWAGGER)
                .map(swaggerFolder -> {
                    OpenAPI swaggerFile = getSwaggerFile(exchange, swaggerFolder.getSwaggerIdentifierAndContent());
                    OpenApiInteractionValidator validator = OpenApiInteractionValidator.createFor(swaggerFile)
                            .withApi(swaggerFile)
                            .withWhitelist(getWhitelist())
                            .build();

                    return validator.validateRequest(simpleRequest);
                });
    }

    private String getValidationErrorMessage(ValidationReport validationReport) {
        return validationReport.getMessages()
                .stream()
                .map(ValidationReport.Message::getMessage)
                .collect(Collectors.joining());
    }

    private ValidationErrorsWhitelist getWhitelist() {
        return ValidationErrorsWhitelist.create()
                .withRule(SWAGGER_PATH, WhitelistRules.pathContainsSubstring(SWAGGER))
                .withRule(SWAGGER_MESSAGE, WhitelistRules.messageContainsSubstring(SWAGGER))
                .withRule(V3_PATH, WhitelistRules.pathContainsSubstring(V3))
                .withRule(V3_MESSAGE, WhitelistRules.messageContainsSubstring(V3));
    }

    private Mono<Void> forwardRequest(ServerWebExchange exchange, GatewayFilterChain chain,
                                      ValidationReport validationReport) {
        if (validationReport.hasErrors()) {
            return Mono.error(
                    new CarRentalResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            getValidationErrorMessage(validationReport)
                    )
            );
        }

        return chain.filter(exchange);
    }

}
