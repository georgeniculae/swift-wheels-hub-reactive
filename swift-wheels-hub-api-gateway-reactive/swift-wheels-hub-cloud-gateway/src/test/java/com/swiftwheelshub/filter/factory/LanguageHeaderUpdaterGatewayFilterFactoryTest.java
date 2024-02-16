package com.swiftwheelshub.filter.factory;

import com.swiftwheelshub.cloudgateway.filter.factory.LanguageHeaderUpdaterGatewayFilterFactory;
import com.swiftwheelshub.util.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@ExtendWith(MockitoExtension.class)
class LanguageHeaderUpdaterGatewayFilterFactoryTest {

    @InjectMocks
    private LanguageHeaderUpdaterGatewayFilterFactory languageHeaderUpdaterGatewayFilterFactory;

    @Test
    void applyTest_success() {
        String tokenValue = TestUtils.getResourceAsJson("/data/JwtToken.json", String.class);

        MockServerHttpRequest request = MockServerHttpRequest.get("/agency/rental-offices/{id}", 1)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue)
                .build();
        MockServerWebExchange.builder(request).build();

        LanguageHeaderUpdaterGatewayFilterFactory.LanguageConfig languageConfig =
                new LanguageHeaderUpdaterGatewayFilterFactory.LanguageConfig("EN");

        GatewayFilter apply = languageHeaderUpdaterGatewayFilterFactory.apply(languageConfig);
        assertNotNull(apply);
    }

}
