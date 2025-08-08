package com.tripsok_back.security.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tripsok_back.repository.RedisBlackListAccessTokenRepository;
import com.tripsok_back.security.jwt.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Log4j2
public class JwtCheckFilter extends OncePerRequestFilter {
	private final JwtUtil jwtUtil;
	private final RedisBlackListAccessTokenRepository blackListAccessTokenRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		String token = authHeader.substring(7);
		if (blackListAccessTokenRepository.existsByToken(token)) {
			log.warn("해당 토큰은 블랙리스트에 있습니다: {}", token);
			return;
		}
		String userId = jwtUtil.validateAndExtract(token, "userId", String.class);
		SecurityContextHolder.getContext()
			.setAuthentication(new UsernamePasswordAuthenticationToken(userId, token, jwtUtil.getAuthorities(token)));
		filterChain.doFilter(request, response);
	}
}
