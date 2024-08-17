package com.swiftwheelshubreactive.agency.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
@Getter
public class MongoMigrationProperties {

    @Value("${migration.packageScan}")
    private String packageScan;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

}
