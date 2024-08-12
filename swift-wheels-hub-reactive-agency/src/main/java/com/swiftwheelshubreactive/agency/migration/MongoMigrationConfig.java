package com.swiftwheelshubreactive.agency.migration;

import com.mongodb.reactivestreams.client.MongoClient;
import io.mongock.driver.mongodb.reactive.driver.MongoReactiveDriver;
import io.mongock.runner.springboot.MongockSpringboot;
import io.mongock.runner.springboot.base.MongockInitializingBeanRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class MongoMigrationConfig {

    @Bean
    public MongockInitializingBeanRunner getBuilder(@Value("${migration.packageScan}") String packageScan,
                                                    @Value("${spring.data.mongodb.database}") String databaseName,
                                                    MongoClient mongoClient,
                                                    ApplicationContext context) {
        return MongockSpringboot.builder()
                .setDriver(MongoReactiveDriver.withDefaultLock(mongoClient, databaseName))
                .addMigrationScanPackage(packageScan)
                .setSpringContext(context)
                .buildInitializingBeanRunner();
    }

}
