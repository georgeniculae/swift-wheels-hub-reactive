package com.swiftwheelshubreactive.requestvalidator.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "swagger")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RegisteredEndpoints {

    private List<RegisteredEndpoint> endpoints;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class RegisteredEndpoint {

        private String identifier;
        private String url;

    }

}
