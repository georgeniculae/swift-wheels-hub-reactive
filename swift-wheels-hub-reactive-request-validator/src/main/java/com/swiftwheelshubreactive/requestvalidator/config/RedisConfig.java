package com.swiftwheelshubreactive.requestvalidator.config;

import com.swiftwheelshubreactive.requestvalidator.model.SwaggerFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${spring.redis.password}")
    private String redisPassword;

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfig = new RedisStandaloneConfiguration();

        redisStandaloneConfig.setHostName(redisHost);
        redisStandaloneConfig.setPort(redisPort);
        redisStandaloneConfig.setPassword(redisPassword);

        return new LettuceConnectionFactory(redisStandaloneConfig);
    }

    @Bean
    public ReactiveRedisOperations<String, SwaggerFile> redisOperations(LettuceConnectionFactory connectionFactory) {
        RedisSerializationContext.RedisSerializationContextBuilder<String, SwaggerFile> serializationContextBuilder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

        serializationContextBuilder.key(new StringRedisSerializer());
        serializationContextBuilder.value(new GenericToStringSerializer<>(SwaggerFile.class));
        serializationContextBuilder.hashKey(new StringRedisSerializer());
        serializationContextBuilder.hashValue(new GenericJackson2JsonRedisSerializer());

        RedisSerializationContext<String, SwaggerFile> serializationContext = serializationContextBuilder.build();

        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
    }

}
