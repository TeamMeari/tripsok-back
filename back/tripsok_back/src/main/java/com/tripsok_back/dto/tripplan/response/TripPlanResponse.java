package com.tripsok_back.dto.tripplan.response;

import java.util.Set;

import com.tripsok_back.dto.tripplan.TripPlanCommonDto;

import io.swagger.v3.oas.annotations.media.Schema;

public record TripPlanResponse(
	@Schema(description = "여행 일정", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	TripPlanCommonDto tripPlan,
	@Schema(description = "방문지점 리스트 순서대로", requiredMode = Schema.RequiredMode.REQUIRED)
	Set<VisitSpotResponse> visitSpotSet
	) {
}
