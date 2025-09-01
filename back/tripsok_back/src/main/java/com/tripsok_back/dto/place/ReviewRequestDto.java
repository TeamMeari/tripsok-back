package com.tripsok_back.dto.place;

import jakarta.validation.constraints.NotBlank;

public class ReviewRequestDto {

	@NotBlank(message = "PlaceId가 비었습니다")
	Integer placeId;
	@NotBlank(message = "리뷰 내용을 입력해주세요")
	String review;
}
