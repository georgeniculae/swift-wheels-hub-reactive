package com.swiftwheelshubreactive.requestvalidator.config;

import com.swiftwheelshubreactive.requestvalidator.model.SwaggerFile;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisOperations<String, SwaggerFile> redisOperations(ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<SwaggerFile> serializer = new Jackson2JsonRedisSerializer<>(SwaggerFile.class);

        RedisSerializationContext.RedisSerializationContextBuilder<String, SwaggerFile> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext<String, SwaggerFile> context = builder.value(serializer).build();

        return new ReactiveRedisTemplate<>(factory, context);
    }

}