package com.swiftwheelshub.service;

import com.swiftwheelshub.cloudgateway.service.SwaggerExtractorService;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SwaggerExtractorServiceTest {

    @InjectMocks
    private SwaggerExtractorService swaggerExtractorService;

    @Mock
    private ResourceLoader resourceLoader;

    @Test
    void getSwaggerIdentifierAndContentTest_success() {
        ReflectionTestUtils.setField(swaggerExtractorService, "swaggerLocation", "src/main/resources/swagger-definitions");

        Map<String, OpenAPI> expectedResult = new HashMap<>();
        expectedResult.put("agency", new OpenAPIV3Parser().read("src/main/resources/swagger-definitions/car-rental-agency.yaml"));
        expectedResult.put("bookings", new OpenAPIV3Parser().read("src/main/resources/swagger-definitions/car-rental-bookings.yaml"));
        expectedResult.put("customers", new OpenAPIV3Parser().read("src/main/resources/swagger-definitions/car-rental-customers.yaml"));
        expectedResult.put("expense", new OpenAPIV3Parser().read("src/main/resources/swagger-definitions/car-rental-expense.yaml"));

        when(resourceLoader.getResource(anyString())).thenReturn(new ClassPathResource("swagger-definitions"));

        StepVerifier.create(swaggerExtractorService.getSwaggerIdentifierAndContent())
                .expectNext(expectedResult)
                .verifyComplete();
    }

    @Test
    void getSwaggerIdentifierAndContentTest_errorOnFindingSwaggerFolder() {
        ReflectionTestUtils.setField(swaggerExtractorService, "swaggerLocation", "test");

        when(resourceLoader.getResource(anyString())).thenReturn(new ClassPathResource("test"));

        StepVerifier.create(swaggerExtractorService.getSwaggerIdentifierAndContent())
                .expectError()
                .verify();
    }

}
