package com.tripsok_back.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tripsok_back.dto.user.UserSignUpRequest;
import com.tripsok_back.dto.user.UserSignUpResponse;
import com.tripsok_back.service.EmailService;
import com.tripsok_back.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	private final EmailService emailService;

	@PostMapping("/signup")
	public ResponseEntity<UserSignUpResponse> signUp(@RequestBody UserSignUpRequest request) {
		// 이메일 인증 코드 검증
		boolean isVerified = emailService.verifyEmailCode(request.getEmail(), request.getSocialType());
		if (!isVerified) {
			throw new
		}
		// 회원가입 처리
		UserSignUpResponse response = userService.signUp(request);
		return ResponseEntity.ok(response);
	}
}
