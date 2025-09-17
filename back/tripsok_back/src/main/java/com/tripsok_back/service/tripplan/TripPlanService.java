package com.tripsok_back.service.tripplan;

import com.tripsok_back.dto.tripplan.request.UpdateTripPlanRequest;
import com.tripsok_back.dto.tripplan.response.TripPlanResponse;
import com.tripsok_back.type.LocaleCode;

public interface TripPlanService {
	TripPlanResponse createOrUpdateTripPlan(Integer userId, UpdateTripPlanRequest request, LocaleCode locale);

	TripPlanResponse getTripPlan(Integer userId, LocaleCode locale);

}
