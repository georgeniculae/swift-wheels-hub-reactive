package com.swiftwheelshubreactive.requestvalidator.service;

import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple4;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SwaggerExtractorService {

    private static final String X_API_KEY = "X-API-KEY";

    private static final String AGENCY = "agency";

    private static final String BOOKINGS = "bookings";

    private static final String CUSTOMERS = "customers";

    private static final String EXPENSE = "expense";

    @Value("${swagger.agency}")
    private String agencyApiDocUrl;

    @Value("${swagger.booking}")
    private String bookingApiDocUrl;

    @Value("${swagger.customer}")
    private String customerApiDocUrl;

    @Value("${swagger.expense}")
    private String expenseApiDocUrl;

    @Value("${apikey.secret}")
    private String apikey;

    private final WebClient webClient;

    public Mono<Map<String, String>> getSwaggerIdentifierAndContent() {
        return Mono.zip(
                        getSwiftWheelsHubRentalAgencySwagger(),
                        getSwiftWheelsHubBookingSwagger(),
                        getSwiftWheelsHubCustomerSwagger(),
                        getSwiftWheelsHubExpenseSwagger()
                )
                .map(this::getSwaggersAndIdentifiers);
    }

    private Map<String, String> getSwaggersAndIdentifiers(Tuple4<String, String, String, String> swaggers) {
        String agencySwaggerContent = swaggers.getT1();
        String bookingSwaggerContent = swaggers.getT2();
        String customerSwaggerContent = swaggers.getT3();
        String expenseSwaggerContent = swaggers.getT4();

        return Map.of(
                AGENCY, agencySwaggerContent,
                BOOKINGS, bookingSwaggerContent,
                CUSTOMERS, customerSwaggerContent,
                EXPENSE, expenseSwaggerContent
        );
    }

    public Mono<String> getSwaggerFileForMicroservice(String microserviceName) {
        return getSwaggerIdentifierAndContent()
                .map(swaggerIdentifierAndContent -> {
                    if (swaggerIdentifierAndContent.containsKey(microserviceName)) {
                        return swaggerIdentifierAndContent.get(microserviceName);
                    }

                    throw new SwiftWheelsHubException("Microservice not existent");
                });
    }

    private Mono<String> getSwiftWheelsHubRentalAgencySwagger() {
        return webClient.get()
                .uri(agencyApiDocUrl)
                .header(X_API_KEY, apikey)
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(Retry.fixedDelay(6, Duration.ofSeconds(10)))
                .filter(StringUtils::isNotBlank)
                .switchIfEmpty(Mono.error(new SwiftWheelsHubException("Swift Wheels Hub Agency swagger is empty")));
    }

    private Mono<String> getSwiftWheelsHubBookingSwagger() {
        return webClient.get()
                .uri(bookingApiDocUrl)
                .header(X_API_KEY, apikey)
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(Retry.fixedDelay(6, Duration.ofSeconds(10)))
                .filter(StringUtils::isNotBlank)
                .switchIfEmpty(Mono.error(new SwiftWheelsHubException("Swift Wheels Hub Booking swagger is empty")));
    }

    private Mono<String> getSwiftWheelsHubCustomerSwagger() {
        return webClient.get()
                .uri(customerApiDocUrl)
                .header(X_API_KEY, apikey)
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(Retry.fixedDelay(6, Duration.ofSeconds(10)))
                .filter(StringUtils::isNotBlank)
                .switchIfEmpty(Mono.error(new SwiftWheelsHubException("Swift Wheels Hub Customers swagger is empty")));
    }

    private Mono<String> getSwiftWheelsHubExpenseSwagger() {
        return webClient.get()
                .uri(expenseApiDocUrl)
                .header(X_API_KEY, apikey)
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(Retry.fixedDelay(6, Duration.ofSeconds(10)))
                .filter(StringUtils::isNotBlank)
                .switchIfEmpty(Mono.error(new SwiftWheelsHubException("Swift Wheels Hub Customers swagger is empty")));
    }

}
