package com.tripsok_back.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
	// Common API error 10000대
	BAD_REQUEST_JSON(HttpStatus.BAD_REQUEST, -10000, "잘못된 JSON 형식입니다."),

	// Auth API error 11000대
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, -11000, "유효하지 않은 토큰입니다."),
	MISSING_SOCIAL_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, -11001, "SocialAccessToken 이 존재하지 않습니다."),
	USER_ALREADY_EXISTS(HttpStatus.CONFLICT, -11002, "이미 존재하는 유저입니다."),
	USER_NOT_REGISTERED(HttpStatus.NOT_FOUND, -11003, "회원가입을 하지 않은 사용자입니다."),
	CONFLICT_NICKNAME(HttpStatus.CONFLICT, -11004, "이미 존재하는 닉네임입니다."),
	AUTHENTICATED_USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, -11005, "인증된 사용자를 찾을 수 없습니다."),
	UNAUTHENTICATED_ACCESS(HttpStatus.UNAUTHORIZED, -11006, "인증정보가 없습니다."),
	EMAIL_VERIFICATION_FAILED(HttpStatus.BAD_REQUEST, -11007, "이메일 인증에 실패했습니다."),
	DUPLICATE_EMAIL(HttpStatus.CONFLICT, -11008, "이미 사용 중인 이메일입니다.");

	private final HttpStatus httpStatus;
	private final int code;
	private final String errorMessage;

	ErrorCode(HttpStatus httpStatus, int code, String errorMessage) {
		this.httpStatus = httpStatus;
		this.code = code;
		this.errorMessage = errorMessage;
	}
}
