package com.autohubreactive.requestvalidator.service;

import com.autohubreactive.lib.retry.RetryHandler;
import com.autohubreactive.requestvalidator.config.RegisteredEndpoints;
import com.autohubreactive.requestvalidator.model.SwaggerFile;
import com.autohubreactive.requestvalidator.util.AssertionUtil;
import com.autohubreactive.requestvalidator.util.TestUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.retry.RetrySpec;

import java.time.Duration;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SwaggerExtractorServiceTest {

    private static String agencyContent;
    private static String aiContent;
    private static String bookingsContent;
    private static String customersContent;
    private static String expenseContent;
    private static SwaggerFile agencySwagger;
    private static SwaggerFile aiSwagger;
    private static SwaggerFile bookingsSwagger;
    private static SwaggerFile customersSwagger;
    private static SwaggerFile expenseSwagger;
    private static List<RegisteredEndpoints.RegisteredEndpoint> endpoints;

    @InjectMocks
    private SwaggerExtractorService swaggerExtractorService;
    @Mock
    private WebClient webClient;
    @Mock
    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;
    @Mock
    private RegisteredEndpoints registeredEndpoints;
    @Mock
    private RetryHandler retryHandler;

    @BeforeAll
    static void setUp() {
        agencyContent =
                TestUtil.getResourceAsJson("/data/AutoHubReactiveAgencySwagger.json", String.class);
        aiContent =
                TestUtil.getResourceAsJson("/data/AutoHubReactiveAiSwagger.json", String.class);
        bookingsContent =
                TestUtil.getResourceAsJson("/data/AutoHubReactiveBookingsSwagger.json", String.class);
        customersContent =
                TestUtil.getResourceAsJson("/data/AutoHubReactiveCustomersSwagger.json", String.class);
        expenseContent =
                TestUtil.getResourceAsJson("/data/AutoHubReactiveExpenseSwagger.json", String.class);

        endpoints = List.of(
                new RegisteredEndpoints.RegisteredEndpoint("agency", "agency-url"),
                new RegisteredEndpoints.RegisteredEndpoint("ai", "ai-url"),
                new RegisteredEndpoints.RegisteredEndpoint("bookings", "bookings-url"),
                new RegisteredEndpoints.RegisteredEndpoint("customers", "customers-url"),
                new RegisteredEndpoints.RegisteredEndpoint("expense", "expense-url")
        );

        agencySwagger = SwaggerFile.builder().identifier("agency").swaggerContent(agencyContent).build();
        aiSwagger = SwaggerFile.builder().identifier("ai").swaggerContent(aiContent).build();
        bookingsSwagger = SwaggerFile.builder().identifier("bookings").swaggerContent(bookingsContent).build();
        customersSwagger = SwaggerFile.builder().identifier("customers").swaggerContent(customersContent).build();
        expenseSwagger = SwaggerFile.builder().identifier("expense").swaggerContent(expenseContent).build();
    }

    @Test
    @SuppressWarnings("all")
    void getSwaggerFilesTest_success() {
        when(registeredEndpoints.getEndpoints()).thenReturn(endpoints);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenAnswer(new Answer() {
            private int count = 0;

            public Object answer(InvocationOnMock invocation) {
                count++;

                if (count == 1) {
                    return Mono.just(agencyContent);
                } else if (count == 2) {
                    return Mono.just(aiContent);
                } else if (count == 3) {
                    return Mono.just(bookingsContent);
                } else if (count == 4) {
                    return Mono.just(customersContent);
                } else {
                    return Mono.just(expenseContent);
                }
            }
        });
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofSeconds(0)));

        swaggerExtractorService.getSwaggerFiles()
                .as(StepVerifier::create)
                .assertNext(swaggerFile -> AssertionUtil.assertSwaggerFile(agencySwagger, swaggerFile))
                .assertNext(swaggerFile -> AssertionUtil.assertSwaggerFile(aiSwagger, swaggerFile))
                .assertNext(swaggerFile -> AssertionUtil.assertSwaggerFile(bookingsSwagger, swaggerFile))
                .assertNext(swaggerFile -> AssertionUtil.assertSwaggerFile(customersSwagger, swaggerFile))
                .assertNext(swaggerFile -> AssertionUtil.assertSwaggerFile(expenseSwagger, swaggerFile))
                .verifyComplete();
    }

    @Test
    @SuppressWarnings("all")
    void getSwaggerFileForMicroservice_success() {
        when(registeredEndpoints.getEndpoints()).thenReturn(endpoints);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenAnswer(new Answer() {
            private int count = 0;

            public Object answer(InvocationOnMock invocation) {
                count++;

                if (count == 1) {
                    return Mono.just(agencyContent);
                } else if (count == 2) {
                    return Mono.just(aiContent);
                } else if (count == 3) {
                    return Mono.just(bookingsContent);
                } else if (count == 4) {
                    return Mono.just(customersContent);
                } else {
                    return Mono.just(expenseContent);
                }
            }
        });
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofSeconds(0)));

        swaggerExtractorService.getSwaggerFileForMicroservice("expense")
                .as(StepVerifier::create)
                .expectNextMatches(actualExpenseSwagger ->
                        expenseSwagger.getIdentifier().equals(actualExpenseSwagger.getIdentifier()) &&
                                expenseSwagger.getSwaggerContent().equals(actualExpenseSwagger.getSwaggerContent()))
                .verifyComplete();
    }

    @Test
    @SuppressWarnings("all")
    void getSwaggerFileForMicroservice_nonexistentMicroservice_error() {
        when(registeredEndpoints.getEndpoints()).thenReturn(endpoints);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenAnswer(new Answer() {
            private int count = 0;

            public Object answer(InvocationOnMock invocation) {
                count++;

                if (count == 1) {
                    return Mono.just(agencyContent);
                } else if (count == 2) {
                    return Mono.just(aiContent);
                } else if (count == 3) {
                    return Mono.just(bookingsContent);
                } else if (count == 4) {
                    return Mono.just(customersContent);
                } else {
                    return Mono.just(expenseContent);
                }
            }
        });
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofSeconds(0)));

        swaggerExtractorService.getSwaggerFileForMicroservice("test")
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

}
