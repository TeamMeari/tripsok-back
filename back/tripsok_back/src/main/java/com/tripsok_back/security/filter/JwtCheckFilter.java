package com.tripsok_back.security.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripsok_back.exception.AuthException;
import com.tripsok_back.exception.ErrorCode;
import com.tripsok_back.exception.JwtException;
import com.tripsok_back.exception.handler.ErrorResponse;
import com.tripsok_back.repository.auth.RedisBlackListAccessTokenRepository;
import com.tripsok_back.security.jwt.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Log4j2
@Component
public class JwtCheckFilter extends OncePerRequestFilter {
	private final JwtUtil jwtUtil;
	private final RedisBlackListAccessTokenRepository blackListAccessTokenRepository;
	private final ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		String token = authHeader.substring(7);
		try {
		if (blackListAccessTokenRepository.existsByToken(token)) {
			throw new AuthException(ErrorCode.INVALID_TOKEN, "해당 토큰은 블랙리스트에 있습니다.");
		}
			Integer userId = jwtUtil.validateAndExtract(token, "userId", Integer.class);
			SecurityContextHolder.getContext()
				.setAuthentication(new UsernamePasswordAuthenticationToken(userId, token, jwtUtil.getAuthorities(token)));
			filterChain.doFilter(request, response);
		}catch (JwtException | AuthException e){
			SecurityContextHolder.clearContext();
			if (response.isCommitted()) {
				return;
			}
			response.reset();
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json;charset=UTF-8");
			ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode().getCode(),
				e.getMessage());
			objectMapper.writeValue(response.getOutputStream(), errorResponse);
		}
	}
}
