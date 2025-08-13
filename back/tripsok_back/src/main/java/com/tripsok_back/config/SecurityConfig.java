package com.tripsok_back.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.tripsok_back.model.user.Role;
import com.tripsok_back.security.filter.JwtCheckFilter;
import com.tripsok_back.security.handler.CustomAccessDeniedHandler;
import com.tripsok_back.security.handler.CustomAuthenticationEntryPoint;
import com.tripsok_back.security.service.TripSokUserDetailsService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration // 스프링 컨테이너에 등록되는 설정 클래스임을 나타냄
@EnableWebSecurity // Spring Security를 활성화하는 어노테이션
public class SecurityConfig {
	private final TripSokUserDetailsService userDetailsService;
	private final JwtCheckFilter jwtFilter;
	private final CustomAccessDeniedHandler customAccessDeniedHandler;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors(it -> it.configurationSource(corsConfigurationSource())).csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(auth -> {
				auth.requestMatchers("/api/v1/auth/**", "/swagger-ui/**", "/v3/api-docs/**")
					.permitAll()
					.requestMatchers("/api/v1/user/**")
					.hasAnyAuthority(Role.USER.getAuthority().getFirst(), Role.ADMIN.getAuthority().getFirst())
					.requestMatchers("/api/v1/admin/**")
					.hasAuthority(Role.ADMIN.getAuthority().getFirst());
			});
		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		http.exceptionHandling(it -> it.accessDeniedHandler(customAccessDeniedHandler)
			.authenticationEntryPoint(customAuthenticationEntryPoint));
		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfig = new CorsConfiguration();
		corsConfig.setAllowedOriginPatterns(
			List.of("http://localhost:8080", "http://localhost:5173", "http://localhost:3000",
				"http://localhost:4173"));
		corsConfig.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "PUT", "OPTIONS"));
		corsConfig.setAllowedHeaders(List.of("Authorization", "Content-Type"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfig);
		return source;
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws
		Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
}
