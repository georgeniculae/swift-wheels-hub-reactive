package com.swiftwheelshubreactive.lib.annotation;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootApplication(exclude = ReactiveUserDetailsServiceAutoConfiguration.class)
@ComponentScan(basePackages = "com.swiftwheelshubreactive")
@EntityScan("com.swiftwheelshubreactive")
@EnableReactiveMongoRepositories(basePackages = "com.swiftwheelshubreactive")
@EnableTransactionManagement
@EnableDiscoveryClient
@EnableScheduling
@EnableConfigurationProperties
@EnableCaching
public @interface SwiftWheelsHubReactiveMicroservice {
}
