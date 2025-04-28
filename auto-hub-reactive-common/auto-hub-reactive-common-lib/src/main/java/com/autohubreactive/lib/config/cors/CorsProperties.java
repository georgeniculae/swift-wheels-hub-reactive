package com.autohubreactive.lib.config.cors;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cors")
@Getter
@Setter
public class CorsProperties {

    private String mapping;
    private String allowedOrigins;
    private String allowedMethods;
    private String allowedHeaders;
    private Integer codecMaxInMemorySizeInMb;

}
