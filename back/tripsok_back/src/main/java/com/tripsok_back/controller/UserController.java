package com.tripsok_back.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tripsok_back.dto.SliceResponse;
import com.tripsok_back.dto.user.request.ChangeContactEmailRequest;
import com.tripsok_back.dto.user.request.ChangeInterestThemeRequest;
import com.tripsok_back.dto.user.request.ChangeUserInfoRequest;
import com.tripsok_back.dto.user.response.UserInfoResponse;
import com.tripsok_back.service.user.UserService;
import com.tripsok_back.type.LocaleCode;
import com.tripsok_back.type.PlaceJoinType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
	private final UserService userService;

	@GetMapping("/info")
	public ResponseEntity<UserInfoResponse> getUserInfo(@AuthenticationPrincipal Integer userId,
		@Parameter(description = "언어(로케일) 코드", example = "KO", schema = @Schema(type = "string", allowableValues = {
			"KO", "EN", "JA", "CN"}))
		@RequestParam(name = "locale", defaultValue = "KO") LocaleCode locale
	) {
		return ResponseEntity.ok().body(userService.getUserInfo(userId, locale));
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

	@Operation(
		summary = "유저 장소 좋아요",
		description = """
			토글 형식으로 동작합니다.
			- 이미 좋아요한 장소일 경우, 좋아요를 취소합니다.
			- 좋아요하지 않은 장소일 경우, 좋아요를 추가합니다.
			"""
	)
	@PostMapping("/like-place/{placeId}")
	public ResponseEntity<Void> likePlace(@AuthenticationPrincipal Integer userId, @PathVariable Integer placeId) {
		userService.likePlace(userId, placeId);
		return ResponseEntity.ok().build();
	}

	@Operation(
		summary = "유저가 좋아요한 장소 목록 조회",
		description = """
			유저가 좋아요한 장소 목록을 조회합니다.
			결과가 0건이어도 200 OK와 빈 content를 반환합니다.
			"""
	)
	@GetMapping("/like-places")
	public ResponseEntity<SliceResponse> getLikedPlaces(@AuthenticationPrincipal Integer userId,
		@Parameter(description = "가져올 데이터 사이즈", example = "20", schema = @Schema(minimum = "10", maximum = "100"))
		@RequestParam(defaultValue = "20") @Min(10) @Max(100) int size,
		@Parameter(description = "마지막으로 조회된 데이터 ID", example = "123")
		@RequestParam(required = false) Integer lastId,
		@Parameter(description = "장소 타입")
		@RequestParam(required = false) PlaceJoinType type,
		@Parameter(description = "언어 코드")
		@RequestParam(defaultValue = "EN", required = false) LocaleCode locale) {
		return ResponseEntity.ok(userService.getUserLikedPlaces(userId, size, lastId, type, locale));
	}

	@PatchMapping("/info")
	public ResponseEntity<Void> changeUserInfo(@AuthenticationPrincipal Integer userId,
		@Valid @RequestBody ChangeUserInfoRequest request) {
		userService.changeUserInfo(userId, request);
		return ResponseEntity.noContent().build();
	}
}
