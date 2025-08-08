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
					.description("Local Server")));
	}

	private Components createSecurityComponents() {
		Components components = new Components();
		SecurityScheme securityScheme = new SecurityScheme()
			.type(SecurityScheme.Type.HTTP)
			.scheme("bearer")
			.in(SecurityScheme.In.HEADER)
			.name(AUTHORIZATION);
		components.addSecuritySchemes(AUTHORIZATION, securityScheme);
		return components;
	}

	private SecurityRequirement createSecurityRequirement() {
		SecurityRequirement securityRequirement = new SecurityRequirement();
		securityRequirement.addList(AUTHORIZATION);
		return securityRequirement;
	}
}
