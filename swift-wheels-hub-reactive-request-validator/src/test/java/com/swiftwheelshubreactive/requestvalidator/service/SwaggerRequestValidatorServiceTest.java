package com.swiftwheelshubreactive.requestvalidator.service;

import com.swiftwheelshubreactive.dto.IncomingRequestDetails;
import com.swiftwheelshubreactive.dto.RequestValidationReport;
import com.swiftwheelshubreactive.requestvalidator.model.SwaggerFile;
import com.swiftwheelshubreactive.requestvalidator.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SwaggerRequestValidatorServiceTest {

    @InjectMocks
    private SwaggerRequestValidatorService swaggerRequestValidatorService;

    @Mock
    private ReactiveRedisOperations<String, SwaggerFile> reactiveRedisOperations;

    @Mock
    private ReactiveValueOperations<String, SwaggerFile> reactiveValueOperations;

    @Test
    void validateRequestTest_success() {
        IncomingRequestDetails incomingRequestDetails =
                TestUtil.getResourceAsJson("/data/IncomingRequestDetails.json", IncomingRequestDetails.class);

        RequestValidationReport validationReport =
                TestUtil.getResourceAsJson("/data/RequestValidationReport.json", RequestValidationReport.class);

        String agencyContent =
                TestUtil.getResourceAsJson("/data/SwiftWheelsHubReactiveAgencySwagger.json", String.class);

        SwaggerFile swaggerFile = SwaggerFile.builder()
                .identifier("agency")
                .swaggerContent(agencyContent)
                .build();

        when(reactiveRedisOperations.opsForValue()).thenReturn(reactiveValueOperations);
        when(reactiveValueOperations.get(anyString())).thenReturn(Mono.just(swaggerFile));

        swaggerRequestValidatorService.validateRequest(incomingRequestDetails)
                .as(StepVerifier::create)
                .expectNext(validationReport)
                .verifyComplete();
    }

}
