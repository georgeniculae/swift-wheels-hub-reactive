package com.swiftwheelshubreactive.requestvalidator.service;

import com.swiftwheelshubreactive.requestvalidator.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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

    @BeforeEach
    public void setupUrls() {
        ReflectionTestUtils.setField(swaggerExtractorService, "agencyApiDocUrl", "/agency");
        ReflectionTestUtils.setField(swaggerExtractorService, "bookingApiDocUrl", "/bookings");
        ReflectionTestUtils.setField(swaggerExtractorService, "customerApiDocUrl", "/customers");
        ReflectionTestUtils.setField(swaggerExtractorService, "expenseApiDocUrl", "/expense");
    }

    @Test
    @SuppressWarnings("all")
    void getSwaggerIdentifierAndContentTest_success() {
        String agencyContent =
                TestUtils.getResourceAsJson("/data/SwiftWheelsHubReactiveAgency.json", String.class);

        String bookingsContent =
                TestUtils.getResourceAsJson("/data/SwiftWheelsHubReactiveBookings.json", String.class);

        String customersContent =
                TestUtils.getResourceAsJson("/data/SwiftWheelsHubReactiveCustomers.json", String.class);

        String expenseContent =
                TestUtils.getResourceAsJson("/data/SwiftWheelsHubReactiveExpense.json", String.class);

        Map<String, String> expected = Map.of(
                "agency", agencyContent,
                "bookings", bookingsContent,
                "customers", customersContent,
                "expense", expenseContent
        );

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
                .expectNext(expected)
                .verifyComplete();
    }

}
