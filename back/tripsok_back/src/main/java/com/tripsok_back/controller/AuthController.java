package com.tripsok_back.controller;

import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tripsok_back.dto.auth.request.EmailLoginRequest;
import com.tripsok_back.dto.auth.request.EmailSignUpRequest;
import com.tripsok_back.dto.auth.request.OauthLoginRequest;
import com.tripsok_back.dto.auth.request.OauthSignUpRequest;
import com.tripsok_back.dto.auth.request.RefreshTokenRequest;
import com.tripsok_back.dto.auth.response.LoginResponseDto;
import com.tripsok_back.dto.auth.response.TokenResponse;
import com.tripsok_back.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

	private final AuthService authService;
	private final String COOKIE_HEARER = "Set-Cookie";
	@PostMapping("/signup/email")
	public ResponseEntity<Void> signUpWithEmail(@RequestBody EmailSignUpRequest request) {
		authService.signUpEmail(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PostMapping("/signup/oauth2")
	public ResponseEntity<LoginResponseDto> signUpWithOAuth2(@RequestBody OauthSignUpRequest request) {
		TokenResponse tokenResponse= authService.signUpOAuth(request);
		return ResponseEntity.status(HttpStatus.CREATED).header(COOKIE_HEARER, getRefreshTokenCookie(tokenResponse.refreshToken()).toString()).body(new LoginResponseDto(tokenResponse.accessToken()));
	}

	@PostMapping("/login/oauth2")
	public ResponseEntity<LoginResponseDto> loginWithOAuth2(@RequestBody OauthLoginRequest request) {
		TokenResponse tokenResponse = authService.loginWithOauth2(request);
		if (tokenResponse.refreshToken() == null) {
			return ResponseEntity.status(HttpStatus.SEE_OTHER).body(new LoginResponseDto(tokenResponse.accessToken()));
		}
		return ResponseEntity.status(HttpStatus.OK).header(COOKIE_HEARER, getRefreshTokenCookie(tokenResponse.refreshToken()).toString()).body(new LoginResponseDto(tokenResponse.accessToken()));
	}

	@PostMapping("/login/email")
	public ResponseEntity<LoginResponseDto> loginWithEmail(@RequestBody EmailLoginRequest request) {
		TokenResponse tokenResponse = authService.loginWithEmail(request.getEmail(), request.getPassword());
		return ResponseEntity.status(HttpStatus.OK).header(COOKIE_HEARER, getRefreshTokenCookie(tokenResponse.refreshToken()).toString()).body(new LoginResponseDto(tokenResponse.accessToken()));
	}

	@PostMapping("/refresh")
	public ResponseEntity<LoginResponseDto> refreshToken(@RequestBody RefreshTokenRequest request) {
		TokenResponse tokenResponse = authService.refresh(request.getRefreshToken());
		return ResponseEntity.status(HttpStatus.OK).header(COOKIE_HEARER, getRefreshTokenCookie(tokenResponse.refreshToken()).toString()).body(new LoginResponseDto(tokenResponse.accessToken()));
	}

	private HttpCookie getRefreshTokenCookie(String refreshToken) {
		return ResponseCookie
			.from("refreshToken", refreshToken)
			.httpOnly(true) // JavaScript 에서 쿠키에 접근할 수 없도록
			.maxAge(authService.getRefreshTokenExpirationTime()) // 쿠키의 만료 시간 설정
			.secure(true) // cookie 가 https 에서만 전송되도록
			.path("/api/auth") // 쿠키가 유효한 경로 설정
			.build();
	}

	//TODO: 쿠키 만료 처리
	private HttpCookie getExpiredCookie() {
		return ResponseCookie
			.from("refreshToken", "")
			.httpOnly(true)
			.maxAge(0)
			.path("/api/auth")
			.build();
	}
}
