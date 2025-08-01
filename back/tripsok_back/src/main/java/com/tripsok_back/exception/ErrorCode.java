package com.tripsok_back.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
	// Common API error 10000대
	BAD_REQUEST_JSON(HttpStatus.BAD_REQUEST, -10000, "잘못된 JSON 형식입니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, -10002, "서버 내부 오류입니다."),
	MISSING_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, -10003, "필수 요청 파라미터가 누락되었습니다."),
	NOT_FOUND_END_POINT(HttpStatus.NOT_FOUND, -10004, "존재하지 않는 엔드포인트입니다."),
	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, -10005, "유효하지 않은 입력 값입니다."),
	INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, -10006, "유효하지 않은 타입의 값입니다."),

	// Auth API error 11000대
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, -11000, "유효하지 않은 토큰입니다."),
	TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, -11001, "토큰이 만료되었습니다."),
	REGISTERED_ALREADY(HttpStatus.CONFLICT, -11002, "이미 해당 방식으로 가입된 사용자입니다."),
	REGISTERED_ANOTHER_SOCIAL(HttpStatus.CONFLICT, -11003, "이미 다른 방식으로 가입된 사용자입니다."),
	CONFLICT_NICKNAME(HttpStatus.CONFLICT, -11004, "이미 존재하는 닉네임입니다."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, -11005, "사용자를 찾을 수 없습니다."),
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
