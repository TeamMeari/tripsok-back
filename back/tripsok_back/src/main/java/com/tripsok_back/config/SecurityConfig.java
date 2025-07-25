package com.tripsok_back.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
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
import com.tripsok_back.security.filter.ApiCheckFilter;
import com.tripsok_back.security.filter.LoginFilter;
import com.tripsok_back.security.handler.CustomAuthenticationEntryPoint;
import com.tripsok_back.security.jwt.JwtUtil;
import com.tripsok_back.security.service.TripSokUserDetailsService;

@Configuration // 스프링 컨테이너에 등록되는 설정 클래스임을 나타냄
@EnableWebSecurity // Spring Security를 활성화하는 어노테이션
public class SecurityConfig {
	private final TripSokUserDetailsService userDetailsService;
	private final JwtUtil jwtUtil;

	public SecurityConfig(TripSokUserDetailsService userDetailsService, JwtUtil jwtUtil) {
		this.userDetailsService = userDetailsService;
		this.jwtUtil = jwtUtil;
	}

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
				auth.requestMatchers("/api/v1/users/**").hasAnyRole(Role.USER.name(), Role.ADMIN.name())
					.requestMatchers("/api/v1/**").permitAll();
			});
		LoginFilter loginFilter = new LoginFilter("/api/v1/auth/signin", jwtUtil);
		loginFilter.setAuthenticationManager(authenticationManager);
		http.addFilterBefore(apiFilter(), UsernamePasswordAuthenticationFilter.class);
		http.addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class);
		http.exceptionHandling(it -> it.authenticationEntryPoint(new CustomAuthenticationEntryPoint()));
		return http.build();
	}

	@Bean
	public ApiCheckFilter apiFilter() {
		return new ApiCheckFilter(jwtUtil);
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
}
