package com.tripsok_back.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InterestPlaceSliceResponse {
	private boolean hasNext;
	private List<?> content;
	@Schema(description = "카테고리가 선택된 경우 해당 카테고리의 관심장소 개수입니다. 선택되지 않은 경우 null입니다.")
	private Integer totalSize;

	public InterestPlaceSliceResponse(boolean hasNext, List<?> content, Integer totalSize) {
		this.hasNext = hasNext;
		this.totalSize =totalSize;
		this.content = content;
	}
}
