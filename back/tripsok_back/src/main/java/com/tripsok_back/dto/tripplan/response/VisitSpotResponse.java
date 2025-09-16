package com.tripsok_back.dto.tripplan.response;

import java.math.BigDecimal;

import com.tripsok_back.model.tripplan.VisitSpot;
import com.tripsok_back.type.LocaleCode;

import io.swagger.v3.oas.annotations.media.Schema;

public record VisitSpotResponse(
	@Schema(description = "방문지점 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	Integer id,
	@Schema(description = "방문지점 이름", example = "경복궁")
	String name,
	@Schema(description = "메모", example = "3시에 도착해서 사진 찍기")
	String memo,
	@Schema(description = "방문지점 주소", example = "서울특별시 종로구 사직로 161")
	String address,
	@Schema(description = "경도", example = "126.9768")
	BigDecimal longitude,
	@Schema(description = "위도", example = "37.5759")
	BigDecimal latitude,
	@Schema(description = "방문 순서", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
Integer orderIndex
){
	public VisitSpotResponse(VisitSpot visitSpot, LocaleCode locale) {
		this(
			visitSpot.getPlace().getId(),
			visitSpot.getPlace().getPlaceTr(locale) != null ? visitSpot.getPlace().getPlaceTr(locale).getPlaceName() : null,
			visitSpot.getMemo(),
			visitSpot.getPlace().getPlaceTr(locale) != null ? visitSpot.getPlace().getPlaceTr(locale).getAddress() : null,
			visitSpot.getPlace().getMapX(),
			visitSpot.getPlace().getMapY(),
			visitSpot.getOrderIndex()
		);
	}
}
