package com.tripsok_back.dto.tripplan.response;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.tripsok_back.dto.tripplan.TripPlanCommonDto;
import com.tripsok_back.model.tripplan.TripPlan;
import com.tripsok_back.model.tripplan.VisitSpot;
import com.tripsok_back.type.LocaleCode;

import io.swagger.v3.oas.annotations.media.Schema;

public record TripPlanResponse(
	@Schema(description = "여행 일정", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	TripPlanCommonDto tripPlan,
	@Schema(description = "방문지점 리스트 순서대로", requiredMode = Schema.RequiredMode.REQUIRED)
	Set<VisitSpotResponse> visitSpotSet
) {
	public TripPlanResponse(TripPlan tripPlan, LocaleCode locale) {
		this(
			new TripPlanCommonDto(tripPlan),
			tripPlan.getVisitSpotSet().stream()
				.sorted(Comparator.comparingInt(VisitSpot::getOrderIndex))
				.map(it -> new VisitSpotResponse(it, locale))
				.collect(Collectors.toCollection(LinkedHashSet::new))
		);
	}
}
