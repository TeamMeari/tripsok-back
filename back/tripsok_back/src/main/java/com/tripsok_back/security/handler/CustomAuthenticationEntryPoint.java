package com.tripsok_back.security.handler;

import static com.tripsok_back.common.constants.CorsConstants.*;
import static com.tripsok_back.exception.ErrorCode.*;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripsok_back.exception.handler.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Log4j2
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
	private final ObjectMapper objectMapper;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException {
		if (response.isCommitted()) {
			return;
		}
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		String requestOrigin = request.getHeader("Origin");
		if (isAllowedOrigin(requestOrigin)) {
			response.setHeader("Access-Control-Allow-Origin", requestOrigin);
			response.setHeader("Access-Control-Allow-Credentials", "true");
			response.setHeader("Access-Control-Allow-Headers", ALLOWED_HEADERS_CSV);
		}
		ErrorResponse errorResponse = new ErrorResponse(UNAUTHENTICATED_ACCESS.getCode(),
			UNAUTHENTICATED_ACCESS.getErrorMessage());
		objectMapper.writeValue(response.getOutputStream(), errorResponse); // ErrorResponse 객체를 JSON으로 변환하여 응답 본문에 작성
	}
}
