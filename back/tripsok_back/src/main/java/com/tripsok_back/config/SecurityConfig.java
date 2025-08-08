package com.tripsok_back.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
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
import com.tripsok_back.repository.RedisBlackListAccessTokenRepository;
import com.tripsok_back.security.filter.JwtCheckFilter;
import com.tripsok_back.security.handler.CustomAuthenticationEntryPoint;
import com.tripsok_back.security.jwt.JwtUtil;
import com.tripsok_back.security.service.TripSokUserDetailsService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration // 스프링 컨테이너에 등록되는 설정 클래스임을 나타냄
@EnableWebSecurity // Spring Security를 활성화하는 어노테이션
public class SecurityConfig {
	private final TripSokUserDetailsService userDetailsService;
	private final JwtUtil jwtUtil;
	private final RedisBlackListAccessTokenRepository blackListAccessTokenRepository;

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(
			AuthenticationManagerBuilder.class);
		authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
		AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
		http.authenticationManager(authenticationManager);
		http.cors(it -> it.configurationSource(corsConfigurationSource())).csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(auth -> {
				auth.requestMatchers("/api/v1/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
					.requestMatchers("/api/v1/user/**").hasAuthority(Role.USER.getAuthority().getFirst())
					.requestMatchers("/api/v1/admin/**").hasAuthority(Role.ADMIN.getAuthority().get(1));
			});
		http.addFilterBefore(apiFilter(), UsernamePasswordAuthenticationFilter.class);
		http.exceptionHandling(it -> it.authenticationEntryPoint(new CustomAuthenticationEntryPoint()));
		return http.build();
	}

	@Bean
	public JwtCheckFilter apiFilter() {
		return new JwtCheckFilter(jwtUtil, blackListAccessTokenRepository);
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
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws
		Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
}
