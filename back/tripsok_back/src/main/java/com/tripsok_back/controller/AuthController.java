package com.tripsok_back.controller;

import org.springframework.http.HttpStatus;
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

	@PostMapping("/signup/email")
	public ResponseEntity<Void> signUpWithEmail(@RequestBody EmailSignUpRequest request) {
		authService.signUpEmail(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PostMapping("/signup/oauth2")
	public ResponseEntity<TokenResponse> signUpWithOAuth2(@RequestBody OauthSignUpRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(authService.signUpOAuth(request));
	}

	@PostMapping("/login/oauth2")
	public ResponseEntity<TokenResponse> loginWithOAuth2(@RequestBody OauthLoginRequest request) {
		TokenResponse tokenResponse = authService.loginWithOauth2(request);
		if (tokenResponse.refreshToken() == null) {
			return ResponseEntity.status(HttpStatus.SEE_OTHER).body(tokenResponse);
		}
		return ResponseEntity.status(HttpStatus.OK).body(tokenResponse);
	}

	@PostMapping("/login/email")
	public ResponseEntity<TokenResponse> loginWithEmail(@RequestBody EmailLoginRequest request) {
		TokenResponse tokenResponse = authService.loginWithEmail(request.getEmail(), request.getPassword());
		return ResponseEntity.status(HttpStatus.OK).body(tokenResponse);
	}

	@PostMapping("/refresh")
	public ResponseEntity<TokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
		TokenResponse tokenResponse = authService.refresh(request.getRefreshToken());
		return ResponseEntity.status(HttpStatus.OK).body(tokenResponse);
	}
}
