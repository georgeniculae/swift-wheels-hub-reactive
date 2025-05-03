package com.autohubreactive.expense.config;

import com.mongodb.reactivestreams.client.MongoClient;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.mongo.reactivestreams.ReactiveStreamsMongoLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableSchedulerLock(defaultLockAtMostFor = "15s")
public class ShedLockConfig {

    public static final String SHEDLOCK = "shedlock";

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Bean
    public LockProvider lockProvider(MongoClient mongoClient) {
        return new ReactiveStreamsMongoLockProvider(mongoClient.getDatabase(databaseName).getCollection(SHEDLOCK));
    }

}
