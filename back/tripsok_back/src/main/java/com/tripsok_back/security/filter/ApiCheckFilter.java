package com.tripsok_back.security.filter;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import com.tripsok_back.security.jwt.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ApiCheckFilter extends OncePerRequestFilter {
	private final JwtUtil jwtUtil;

	public ApiCheckFilter(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		filterChain.doFilter(request, response);
	}

	private boolean checkAuthHeader(HttpServletRequest request) { //TODO 사용하기
		String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			log.error("Authorization heade가 없거나 잘못된 형식입니다");
			return false;
		}
		String token = authHeader.substring(7);
		String userId = jwtUtil.validateAndExtract(token);
		if (userId.isBlank()) {
			log.error("JWT 토큰이 유효하지 않습니다");
			return false;
		}
		return true;
	}
}
