package com.tripsok_back.config;

import static org.springframework.http.HttpHeaders.*;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {
	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.components(createSecurityComponents())
			.addSecurityItem(createSecurityRequirement())
			.info(new Info()
				.title("Tripsok API")
				.description("Tripsok API 명세서")
				.version("v1.0.0"))
			.servers(List.of(
				new Server()
					.url("http://localhost:8080")
					.description("Local Server"),
				new Server()
					.url("https://trip-sok.jayden-bin.cc")
					.description("Remote Server")));
	}

	private Components createSecurityComponents() {
		return new Components()
			.addSecuritySchemes(AUTHORIZATION, new SecurityScheme()
				.type(SecurityScheme.Type.HTTP)
				.scheme("bearer")
				.in(SecurityScheme.In.HEADER)
				.name(AUTHORIZATION));
	}

	private SecurityRequirement createSecurityRequirement() {
		return new SecurityRequirement().addList(AUTHORIZATION);
	}
}
