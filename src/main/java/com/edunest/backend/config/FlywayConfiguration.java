package com.edunest.backend.config;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;

@Configuration
public class FlywayConfiguration {

    @Bean
    public FlywayMigrationInitializer flywayMigrationInitializer(
            org.flywaydb.core.Flyway flyway) {
        return new FlywayMigrationInitializer(flyway);
    }
}
