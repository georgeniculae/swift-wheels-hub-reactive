package com.swiftwheelshubreactive.requestvalidator.service;

import com.swiftwheelshubreactive.requestvalidator.config.RegisteredEndpoints;
import com.swiftwheelshubreactive.requestvalidator.util.TestUtils;
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
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SwaggerExtractorServiceTest {

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

    @Test
    @SuppressWarnings("all")
    void getSwaggerIdentifierAndContentTest_success() {
        String agencyContent =
                TestUtils.getResourceAsJson("/data/SwiftWheelsHubReactiveAgencySwagger.json", String.class);

        String bookingsContent =
                TestUtils.getResourceAsJson("/data/SwiftWheelsHubReactiveBookingsSwagger.json", String.class);

        String customersContent =
                TestUtils.getResourceAsJson("/data/SwiftWheelsHubReactiveCustomersSwagger.json", String.class);

        String expenseContent =
                TestUtils.getResourceAsJson("/data/SwiftWheelsHubReactiveExpenseSwagger.json", String.class);

        Map<String, String> endpoints = new LinkedHashMap<>();
        endpoints.put("agency", agencyContent);
        endpoints.put("bookings", bookingsContent);
        endpoints.put("customers", customersContent);
        endpoints.put("expense", expenseContent);

        List<Tuple2<String, String>> expected = List.of(
                Tuples.of("agency", agencyContent),
                Tuples.of("bookings", bookingsContent),
                Tuples.of("customers", customersContent),
                Tuples.of("expense", expenseContent)
        );

        when(registeredEndpoints.getEndpoints()).thenReturn(endpoints);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), any(String[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.bodyToMono(String.class)).thenAnswer(new Answer() {
            private int count = 0;

            public Object answer(InvocationOnMock invocation) {
                count++;

                if (count == 1) {
                    return Mono.just(agencyContent);
                } else if (count == 2) {
                    return Mono.just(bookingsContent);
                } else if (count == 3) {
                    return Mono.just(customersContent);
                } else {
                    return Mono.just(expenseContent);
                }
            }
        });

        swaggerExtractorService.getSwaggerIdentifierAndContent()
                .as(StepVerifier::create)
                .expectNext(expected.getFirst())
                .expectNext(expected.get(1))
                .expectNext(expected.get(2))
                .expectNext(expected.getLast())
                .verifyComplete();
    }

}
