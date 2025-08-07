package com.tripsok_back.security.handler;

import static com.tripsok_back.exception.ErrorCode.*;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripsok_back.exception.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		ObjectMapper objectMapper = new ObjectMapper();
		ErrorResponse errorResponse = new ErrorResponse(UNAUTHENTICATED_ACCESS.getCode(),
			UNAUTHENTICATED_ACCESS.getErrorMessage());
		objectMapper.writeValue(response.getOutputStream(), errorResponse); // ErrorResponse 객체를 JSON으로 변환하여 응답 본문에 작성
	}
}
