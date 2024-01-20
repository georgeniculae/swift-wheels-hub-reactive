package com.swiftwheelshub.cloudgateway.redis;

import com.swiftwheelshub.cloudgateway.model.SwaggerFolder;
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
    public ReactiveRedisOperations<String, SwaggerFolder> redisOperations(ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<SwaggerFolder> serializer = new Jackson2JsonRedisSerializer<>(SwaggerFolder.class);

        RedisSerializationContext.RedisSerializationContextBuilder<String, SwaggerFolder> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext<String, SwaggerFolder> context = builder.value(serializer).build();

        return new ReactiveRedisTemplate<>(factory, context);
    }

}
