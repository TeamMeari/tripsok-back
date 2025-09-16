package com.tripsok_back.dto.tripplan.request;

import java.util.Set;

import com.tripsok_back.dto.tripplan.TripPlanCommonDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UpdateTripPlanRequest(
	@Schema(description = "수정할 여행 일정 정보", requiredMode = Schema.RequiredMode.REQUIRED)
	TripPlanCommonDto tripPlan,
	@Schema(description = "일정에 추가/수정할 방문지점 리스트(최소 1개 이상, 존재하지 않는 장소ID는 무시됨)", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "방문지점 리스트는 emptySet일 수 있지만 null일 수 없습니다.")
	Set<UpdateVisitSpotRequest> visitSpotSet) {
}
