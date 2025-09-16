package com.tripsok_back.dto.tripplan.request;

import java.util.Set;

import com.tripsok_back.dto.tripplan.TripPlanCommonDto;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateTripPlanRequest (
	@Schema(description = "수정할 여행 일정 정보", requiredMode = Schema.RequiredMode.REQUIRED)
	TripPlanCommonDto tripPlan,
	@Schema(description = "일정에 추가/수정할 방문지점 리스트(최소 1개 이상, 존재하지 않는 장소ID는 무시됨)", requiredMode = Schema.RequiredMode.REQUIRED)
	Set<UpdateVisitSpotRequest> visitSpotSet){}
