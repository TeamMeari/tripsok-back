package com.tripsok_back.controller;

import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tripsok_back.dto.auth.request.EmailLoginRequest;
import com.tripsok_back.dto.auth.request.EmailSignUpRequest;
import com.tripsok_back.dto.auth.request.NicknameDuplicateCheckRequest;
import com.tripsok_back.dto.auth.request.OauthLoginRequest;
import com.tripsok_back.dto.auth.request.OauthSignUpRequest;
import com.tripsok_back.dto.auth.request.ResetPasswordRequest;
import com.tripsok_back.dto.auth.response.LoginResponse;
import com.tripsok_back.dto.auth.response.NicknameDuplicateCheckResponse;
import com.tripsok_back.dto.auth.response.TokenResponse;
import com.tripsok_back.exception.AuthException;
import com.tripsok_back.exception.ErrorCode;
import com.tripsok_back.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

	private final AuthService authService;
	private final String COOKIE_HEARER = HttpHeaders.SET_COOKIE;

	@PostMapping("/signup/email")
	public ResponseEntity<Void> signUpWithEmail(@Valid @RequestBody EmailSignUpRequest request) {
		authService.signUpEmail(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PostMapping("/signup/oauth2")
	public ResponseEntity<LoginResponse> signUpWithOAuth2(@Valid @RequestBody OauthSignUpRequest request) {
		TokenResponse tokenResponse = authService.signUpOAuth(request);
		return ResponseEntity.status(HttpStatus.CREATED)
			.header(COOKIE_HEARER, getRefreshTokenCookie(tokenResponse.refreshToken()).toString())
			.body(new LoginResponse(tokenResponse.accessToken()));
	}

	@PostMapping("/login/oauth2")
	public ResponseEntity<LoginResponse> loginWithOAuth2(@Valid @RequestBody OauthLoginRequest request) {
		TokenResponse tokenResponse = authService.loginWithOauth2(request);
		if (tokenResponse.refreshToken() == null) {
			return ResponseEntity.status(HttpStatus.SEE_OTHER).body(new LoginResponse(tokenResponse.accessToken()));
		}
		return ResponseEntity.status(HttpStatus.OK)
			.header(COOKIE_HEARER, getRefreshTokenCookie(tokenResponse.refreshToken()).toString())
			.body(new LoginResponse(tokenResponse.accessToken()));
	}

	@PostMapping("/login/email")
	public ResponseEntity<LoginResponse> loginWithEmail(@Valid @RequestBody EmailLoginRequest request) {
		TokenResponse tokenResponse = authService.loginWithEmail(request.getEmail(), request.getPassword());
		return ResponseEntity.status(HttpStatus.OK)
			.header(COOKIE_HEARER, getRefreshTokenCookie(tokenResponse.refreshToken()).toString())
			.body(new LoginResponse(tokenResponse.accessToken()));
	}

	@PostMapping("/refresh")
	public ResponseEntity<LoginResponse> refreshToken(@CookieValue String refreshToken) {
		TokenResponse tokenResponse = authService.refresh(refreshToken);
		return ResponseEntity.status(HttpStatus.OK)
			.header(COOKIE_HEARER, getRefreshTokenCookie(tokenResponse.refreshToken()).toString())
			.body(new LoginResponse(tokenResponse.accessToken()));
	}

	// 닉네임 중복 확인
	@PostMapping("/validate/nickname")
	public ResponseEntity<NicknameDuplicateCheckResponse> nicknameDuplicateCheck(
		@Valid @RequestBody NicknameDuplicateCheckRequest request) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(new NicknameDuplicateCheckResponse(authService.nicknameDuplicateCheck(request.getNickname())));
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			throw new AuthException(ErrorCode.INVALID_TOKEN);
		}
		String accessToken = authHeader.substring(7);
		authService.logout(accessToken);
		return ResponseEntity.status(HttpStatus.NO_CONTENT)
			.header(COOKIE_HEARER, getExpiredCookie().toString())
			.build();
	}

	@PostMapping("/reset/password")
	public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
		authService.resetPassword(request.getEmailVerifyToken(), request.getPassword());
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	private HttpCookie getRefreshTokenCookie(String refreshToken) {
		return ResponseCookie
			.from("refreshToken", refreshToken)
			.httpOnly(true) // JavaScript 에서 쿠키에 접근할 수 없도록
			.maxAge(authService.getRefreshTokenExpirationTime() * 60) // 쿠키의 만료 시간 설정
			.secure(true) // cookie 가 https 에서만 전송되도록
			.path("/api/v1/auth") // 쿠키가 유효한 경로 설정
			.build();
	}

	private HttpCookie getExpiredCookie() {
		return ResponseCookie
			.from("refreshToken", "")
			.httpOnly(true)
			.maxAge(0)
			.path("/api/v1/auth")
			.build();
	}
}
