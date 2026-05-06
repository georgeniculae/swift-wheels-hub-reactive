package com.autohubreactive.lib.config.mongodb;

import com.autohubreactive.lib.util.Constants;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.ReactiveHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Configuration
@ConditionalOnBean(name = "mongoProperties")
public class ReactiveMongoHealthIndicatorConfig {

    @Bean(name = "reactiveMongoHealthIndicator")
    public ReactiveHealthIndicator mongoCustomHealthIndicator(
            ReactiveMongoTemplate reactiveMongoTemplate,
            @Value("${spring.mongodb.database}") String database
    ) {
        return () -> reactiveMongoTemplate.executeCommand(new Document(Constants.PING, 1))
                .timeout(Duration.ofSeconds(5))
                .map(document -> getHealthUpDetails(database, document))
                .onErrorResume(e -> getHealthDownDetails(database, e));
    }

    private Health getHealthUpDetails(String database, Document document) {
        return Health.up()
                .withDetail(Constants.COMMAND, Constants.PING)
                .withDetail(Constants.DATABASE, database)
                .withDetail(Constants.OK, document.get(Constants.OK))
                .build();
    }

    private Mono<Health> getHealthDownDetails(String database, Throwable e) {
        return Mono.just(Health.down(e)
                .withDetail(Constants.COMMAND, Constants.PING)
                .withDetail(Constants.DATABASE, database)
                .build());
    }

}
