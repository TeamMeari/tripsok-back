package com.tripsok_back.exception;

import static com.tripsok_back.common.constants.RegexConstants.*;

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
	UNAUTHENTICATED_ACCESS(HttpStatus.UNAUTHORIZED, -11006, "인증되지 않은 접근입니다."),
	EXPIRED_SOCIAL_SIGNUP_TOKEN(HttpStatus.UNAUTHORIZED, -11007, "소셜 회원가입 토큰이 만료되었습니다. 다시 소셜 회원가입을 요청해주세요."),
	EXPIRED_EMAIL_VERIFICATION_TOKEN(HttpStatus.BAD_REQUEST, -11008, "이메일 인증 토큰이 만료되었습니다. 다시 이메일 인증을 요청해주세요."),
	UNSUPPORTED_SOCIAL_TYPE(HttpStatus.BAD_REQUEST, -11009, "지원하지 않는 소셜 타입입니다."),
	INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, -11010, PASSWORD_MESSAGE),
	INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, -11011, "유효하지 않은 리프레시 토큰입니다."),
	INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, -11012, "잘못된 이메일 또는 비밀번호입니다."),
	INVALID_SOCIAL_SIGNUP_TOKEN(HttpStatus.UNAUTHORIZED, -11013, "유효하지 않은 소셜 회원가입 토큰입니다."),
	INVALID_SOCIAL_CODE(HttpStatus.BAD_REQUEST, -11014, "유효하지 않은 소셜 코드입니다."),
	INVALID_NICKNAME_FORMAT(HttpStatus.BAD_REQUEST, -11015, NICKNAME_MESSAGE),
	FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, -11016, "접근이 금지된 사용자입니다."),
	FAILED_DELETE_USER(HttpStatus.INTERNAL_SERVER_ERROR, -11017, "사용자 탈퇴에 실패했습니다."),
	REQUIRED_PASSWORD(HttpStatus.BAD_REQUEST, -11018, "비밀번호는 필수 입력값입니다."),
	REQUIRED_EMAIL_VERIFICATION_TOKEN(HttpStatus.BAD_REQUEST, -11019, "이메일 인증 토큰은 필수 입력값입니다."),

	// Email-verification API error 12000대
	EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, -12000, "이메일 전송에 실패했습니다."),
	EMAIL_VERIFICATION_CODE_INVALID(HttpStatus.BAD_REQUEST, -12001, "유효하지 않은 이메일 인증 코드입니다."),

	//Place API 13000대
	PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, -13000, "존재하지 않는 장소입니다"),
	INVALID_TOUR_TYPE(HttpStatus.BAD_REQUEST, -13001, "올바르지 않은 투어 타입입니다"),

	//TripPlan API 14000대
	TRIP_PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, -14000, "존재하지 않는 여행 계획입니다."),

	// Booking API 15000대;
	BOOKING_TRIP_PLAN_MISMATCH(HttpStatus.BAD_REQUEST, -15000, "예약 요청한 여행 계획이 사용자와 일치하지 않습니다."),
	BOOKING_SPOT_NOT_FOUND(HttpStatus.NOT_FOUND, -15001, "존재하지 않는 예약 장소입니다."),
	NOT_FOUND_BOOKING_TRIP_PLAN(HttpStatus.BAD_REQUEST, -15002, "예약할 여행 계획을 찾을 수 없습니다."),
	INCOMPLETE_TRIP_PLAN(HttpStatus.BAD_REQUEST, -15003, "여행 계획이 완성되지 않았습니다. 여행 계획을 완성한 후 예약을 진행해주세요."),
	BOOKING_PAST_TRIP_DATE(HttpStatus.BAD_REQUEST, -15004, "여행 출발일은 오늘 이후여야합니다."),

	// redis lock error 16000대
	REDISSON_LOCK_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, -16000, "Redis Lock 획득에 실패했습니다. 잠시 후 다시 시도해주세요.");
	private final HttpStatus httpStatus;
	private final int code;
	private final String errorMessage;

	ErrorCode(HttpStatus httpStatus, int code, String errorMessage) {
		this.httpStatus = httpStatus;
		this.code = code;
		this.errorMessage = errorMessage;
	}
}
