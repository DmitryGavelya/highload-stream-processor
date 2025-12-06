package org.hsse.highloadstreamprocessor.enrichment.config;

import io.mongock.driver.mongodb.springdata.v4.SpringDataMongoV4Driver;
import io.mongock.runner.springboot.MongockSpringboot;
import io.mongock.runner.springboot.base.MongockApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongockConfig {
    private static final String MIGRATION_PATH = "org.hsse.highloadstreamprocessor.enrichment.migration";

    @Bean
    public MongockApplicationRunner mongockApplicationRunner(ApplicationContext context, MongoTemplate mongoTemplate) {
        SpringDataMongoV4Driver driver = SpringDataMongoV4Driver.withDefaultLock(mongoTemplate);

        return MongockSpringboot.builder()
                .setSpringContext(context)
                .setDriver(driver)
                .addMigrationScanPackage(MIGRATION_PATH)
                .buildApplicationRunner();
    }
}