package com.tripsok_back.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

	@Bean
	FlywayMigrationStrategy repairMigrationStrategy() {
		return Flyway::migrate;
	}
}
