package com.tripsok_back.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tripsok_back.dto.user.request.ChangeContactEmailRequest;
import com.tripsok_back.dto.user.request.ChangeInterestThemeRequest;
import com.tripsok_back.dto.user.response.UserInfoResponse;
import com.tripsok_back.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
	private final UserService userService;

	@GetMapping("/info")
	public ResponseEntity<UserInfoResponse> getUserInfo(@AuthenticationPrincipal Integer userId) {
		return ResponseEntity.ok().body(userService.getUserInfo(userId));
	}

	@PatchMapping("/contact-email")
	public ResponseEntity<Void> changeContactEmail(@AuthenticationPrincipal Integer userId,
		@Valid @RequestBody ChangeContactEmailRequest request) {
		userService.changeContactEmail(userId, request.getEmailVerifyToken());
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/interest-themes")
	public ResponseEntity<Void> changeInterestThemes(@AuthenticationPrincipal Integer userId,
		@Valid @RequestBody ChangeInterestThemeRequest request) {
		userService.changeInterestThemes(userId, request.getInterestThemeIds());
		return ResponseEntity.noContent().build();
	}
}
