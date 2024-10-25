package com.swiftwheelshub.ai.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.ai.ollama.chat.options")
@Getter
@Setter
public class ChatProperties {

    private String model;
    private Double temperature;

}
