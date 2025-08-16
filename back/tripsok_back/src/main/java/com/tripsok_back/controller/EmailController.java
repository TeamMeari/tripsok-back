package com.tripsok_back.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tripsok_back.dto.email.request.EmailSendRequest;
import com.tripsok_back.dto.email.request.EmailVerifyRequest;
import com.tripsok_back.dto.email.response.EmailVerifyResponse;
import com.tripsok_back.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/email")
public class EmailController {
	private final EmailService emailService;

	@PostMapping("/send")
	public ResponseEntity<Void> sendEmail(@RequestBody EmailSendRequest emailRequest) {
		emailService.sendVerificationEmail(emailRequest.email());
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@PostMapping("/verify")
	public ResponseEntity<EmailVerifyResponse> verifyEmail(@RequestBody EmailVerifyRequest emailVerifyRequest) {
		return ResponseEntity.ok(emailService.verifyEmailCode(emailVerifyRequest.email(), emailVerifyRequest.code()));
	}
}
