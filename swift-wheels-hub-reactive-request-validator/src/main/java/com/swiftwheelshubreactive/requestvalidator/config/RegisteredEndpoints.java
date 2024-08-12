package com.swiftwheelshubreactive.requestvalidator.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "swagger")
@Getter
public class RegisteredEndpoints {

    private Map<String, String> endpoints;

}
