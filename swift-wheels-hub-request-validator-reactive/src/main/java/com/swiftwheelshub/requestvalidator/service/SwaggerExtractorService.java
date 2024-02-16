//package com.swiftwheelshub.requestvalidator.service;
//
//import com.swiftwheelshub.exception.SwiftWheelsHubException;
//import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestClient;
//
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class SwaggerExtractorService {
//
//    private static final String X_API_KEY = "X-API-KEY";
//
//    private static final String AGENCY = "agency";
//
//    private static final String BOOKINGS = "bookings";
//
//    private static final String CUSTOMERS = "customers";
//
//    private static final String EXPENSE = "expense";
//
//    @Value("${swagger.agency}")
//    private String agencyApiDocUrl;
//
//    @Value("${swagger.booking}")
//    private String bookingApiDocUrl;
//
//    @Value("${swagger.customer}")
//    private String customerApiDocUrl;
//
//    @Value("${swagger.expense}")
//    private String expenseApiDocUrl;
//
//    @Value("${apikey.secret}")
//    private String apikey;
//
//    private final RestClient restClient;
//
//    public Map<String, String> getSwaggerIdentifierAndContent() {
//        Map<String, String> agencySwagger = getSwiftWheelsHubRentalAgencySwagger();
//        Map<String, String> bookingSwagger = getSwiftWheelsHubBookingSwagger();
//        Map<String, String> customerSwagger = getSwiftWheelsHubCustomerSwagger();
//        Map<String, String> expenseSwagger = getSwiftWheelsHubExpenseSwagger();
//
//        return getSwaggersAndIdentifiers(agencySwagger, bookingSwagger, customerSwagger, expenseSwagger);
//    }
//
//    private Map<String, String> getSwaggersAndIdentifiers(Map<String, String> agencySwagger,
//                                                          Map<String, String> bookingSwagger,
//                                                          Map<String, String> customerSwagger,
//                                                          Map<String, String> expenseSwagger) {
//        Map<String, String> swaggersAndIdentifiers = new HashMap<>();
//
//        swaggersAndIdentifiers.putAll(agencySwagger);
//        swaggersAndIdentifiers.putAll(bookingSwagger);
//        swaggersAndIdentifiers.putAll(customerSwagger);
//        swaggersAndIdentifiers.putAll(expenseSwagger);
//
//        return Collections.unmodifiableMap(swaggersAndIdentifiers);
//    }
//
//    public Map<String, String> getSwaggerFileForMicroservice(String microserviceName) {
//        return switch (microserviceName) {
//            case AGENCY -> getSwiftWheelsHubRentalAgencySwagger();
//            case BOOKINGS -> getSwiftWheelsHubBookingSwagger();
//            case CUSTOMERS -> getSwiftWheelsHubCustomerSwagger();
//            case EXPENSE -> getSwiftWheelsHubExpenseSwagger();
//            default -> throw new SwiftWheelsHubException("Microservice not existent");
//        };
//    }
//
//    private Map<String, String> getSwiftWheelsHubRentalAgencySwagger() {
//        String body = getRestCallResponse(agencyApiDocUrl);
//
//        String openApiContent = Optional.ofNullable(body)
//                .filter(StringUtils::isNotBlank)
//                .orElseThrow(() -> new SwiftWheelsHubException("Swift Wheels Hub Agency swagger is empty"));
//
//        return Map.of(AGENCY, openApiContent);
//    }
//
//    private Map<String, String> getSwiftWheelsHubBookingSwagger() {
//        String body = getRestCallResponse(bookingApiDocUrl);
//
//        String openApiContent = Optional.ofNullable(body)
//                .filter(StringUtils::isNotBlank)
//                .orElseThrow(() -> new SwiftWheelsHubNotFoundException("Swift Wheels Hub Booking swagger is empty"));
//
//        return Map.of(BOOKINGS, openApiContent);
//    }
//
//    private Map<String, String> getSwiftWheelsHubCustomerSwagger() {
//        String body = getRestCallResponse(customerApiDocUrl);
//
//        String openApiContent = Optional.ofNullable(body)
//                .filter(StringUtils::isNotBlank)
//                .orElseThrow(() -> new SwiftWheelsHubException("Swift Wheels Hub Customers swagger is empty"));
//
//        return Map.of(CUSTOMERS, openApiContent);
//    }
//
//    private Map<String, String> getSwiftWheelsHubExpenseSwagger() {
//        String body = getRestCallResponse(expenseApiDocUrl);
//
//        String openApiContent = Optional.ofNullable(body)
//                .filter(StringUtils::isNotBlank)
//                .orElseThrow(() -> new SwiftWheelsHubException("Swift Wheels Hub Customers swagger is empty"));
//
//        return Map.of(EXPENSE, openApiContent);
//    }
//
//    private String getRestCallResponse(String url) {
//        return restClient.get()
//                .uri(url)
//                .header(X_API_KEY, apikey)
//                .retrieve()
//                .body(String.class);
//    }
//
//}
