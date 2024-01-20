package com.swiftwheelshub.cloudgateway.model;

import io.swagger.v3.oas.models.OpenAPI;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Map;

@RedisHash("SwaggerFolder")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class SwaggerFolder {

    @Id
    private String id;
    private Map<String, OpenAPI> swaggerIdentifierAndContent;

}
