package com.autohubreactive.requestvalidator.handler;

import com.autohubreactive.dto.common.IncomingRequestDetails;
import com.autohubreactive.dto.common.RequestValidationReport;
import com.autohubreactive.requestvalidator.service.RedisService;
import com.autohubreactive.requestvalidator.service.SwaggerRequestValidatorService;
import com.autohubreactive.requestvalidator.util.TestUtil;
import com.autohubreactive.requestvalidator.validator.IncomingRequestDetailsValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestValidatorHandlerTest {

    @InjectMocks
    private RequestValidatorHandler requestValidatorHandler;

    @Mock
    private SwaggerRequestValidatorService swaggerRequestValidatorService;

    @Mock
    private RedisService redisService;

    @Mock
    private IncomingRequestDetailsValidator incomingRequestDetailsValidator;

    @Test
    void validateRequestTest_success() {
        IncomingRequestDetails incomingRequestDetails =
                TestUtil.getResourceAsJson("/data/IncomingRequestDetails.json", IncomingRequestDetails.class);

        RequestValidationReport validationReport =
                TestUtil.getResourceAsJson("/data/RequestValidationReport.json", RequestValidationReport.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.POST)
                .body(Mono.just(incomingRequestDetails));

        when(incomingRequestDetailsValidator.validateBody(any(IncomingRequestDetails.class)))
                .thenReturn(Mono.just(incomingRequestDetails));
        when(swaggerRequestValidatorService.validateRequest(any(IncomingRequestDetails.class)))
                .thenReturn(Mono.just(validationReport));

        requestValidatorHandler.validateRequest(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void repopulateRedisWithSwaggerFilesTest_success() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.POST)
                .body(Mono.just("booking"));

        when(redisService.repopulateRedisWithSwaggerFiles(anyString())).thenReturn(Mono.just(true));

        requestValidatorHandler.repopulateRedisWithSwaggerFiles(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void repopulateRedisWithSwaggerFilesTest_error_emptyRequestBody() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.POST)
                .body(Mono.just(""));

        requestValidatorHandler.repopulateRedisWithSwaggerFiles(serverRequest)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

}
