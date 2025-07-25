package com.tripsok_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.tripsok_back.config.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class})
public class TripsokBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(TripsokBackApplication.class, args);
	}

}
