package com.swiftwheelshubreactive.requestvalidator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("SwaggerFile")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class SwaggerFile {

    private String identifier;
    private String swaggerContent;

}
