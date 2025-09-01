package com.tripsok_back.exception;

import lombok.Getter;

@Getter
public enum InternalErrorCode {
	// Category 에러
	CATEGORY_NOT_FOUND("해당 카테고리가 존재하지 않습니다"),
	PLACE_DETAIL_NOT_FOUND("장소의 세부 정보가 TOUR API에 존재하지 않습니다"),
	JSON_PARSE_ERROR("JSON 변환 중 오류가 발생했습니다"),
	INTERNAL_SERVER_ERROR("알수 없는 오류가 발생했습니다");
	private final String errorMessage;

	InternalErrorCode(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
