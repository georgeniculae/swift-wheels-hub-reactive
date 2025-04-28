package com.autohubreactive.agency.migration;

import com.mongodb.reactivestreams.client.MongoClient;
import com.autohubreactive.agency.config.MongoMigrationProperties;
import io.mongock.driver.mongodb.reactive.driver.MongoReactiveDriver;
import io.mongock.runner.springboot.MongockSpringboot;
import io.mongock.runner.springboot.base.MongockInitializingBeanRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class MongoMigrationConfig {

    @Bean
    public MongockInitializingBeanRunner getBuilder(MongoMigrationProperties mongoMigrationProperties,
                                                    MongoClient mongoClient,
                                                    ApplicationContext context) {
        return MongockSpringboot.builder()
                .setDriver(MongoReactiveDriver.withDefaultLock(mongoClient, mongoMigrationProperties.getDatabaseName()))
                .addMigrationScanPackage(mongoMigrationProperties.getPackageScan())
                .setSpringContext(context)
                .buildInitializingBeanRunner();
    }

}
