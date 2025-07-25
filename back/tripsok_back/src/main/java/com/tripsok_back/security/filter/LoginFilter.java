package com.tripsok_back.security.filter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import com.tripsok_back.security.dto.TripSokUserDto;
import com.tripsok_back.security.jwt.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginFilter extends AbstractAuthenticationProcessingFilter {
	private final JwtUtil jwtUtil;

	public LoginFilter(String defaultFilterProcessesUrl, JwtUtil jwtUtil) {
		super(defaultFilterProcessesUrl);
		this.jwtUtil = jwtUtil;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email,
			password);
		return getAuthenticationManager().authenticate(authenticationToken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		Authentication authResult) {
		String userId = ((TripSokUserDto)authResult.getPrincipal()).getUserId();
		try {
			String token = jwtUtil.generateAccessToken(userId, authResult.getAuthorities());
			response.addHeader("Authorization", "Bearer " + token);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
