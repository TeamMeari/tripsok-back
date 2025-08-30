package com.tripsok_back.dto.place;

import jakarta.validation.constraints.NotNull;

public class ReviewRequestDto {

	@NotNull(message = "PlaceId가 비었습니다")
	Integer placeId;
	@NotNull(message = "리뷰 내용을 입력해주세요")
	String review;
}
