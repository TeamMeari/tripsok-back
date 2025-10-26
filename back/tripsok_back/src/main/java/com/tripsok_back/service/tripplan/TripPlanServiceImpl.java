package com.tripsok_back.service.tripplan;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tripsok_back.dto.tripplan.request.UpdateTripPlanRequest;
import com.tripsok_back.dto.tripplan.request.UpdateVisitSpotRequest;
import com.tripsok_back.dto.tripplan.response.TripPlanResponse;
import com.tripsok_back.exception.ErrorCode;
import com.tripsok_back.exception.TripPlanException;
import com.tripsok_back.model.place.Place;
import com.tripsok_back.model.tripplan.TripPlan;
import com.tripsok_back.model.tripplan.VisitSpot;
import com.tripsok_back.model.user.TripSokUser;
import com.tripsok_back.repository.tripplan.TripPlanRepository;
import com.tripsok_back.service.place.PlaceService;
import com.tripsok_back.service.user.UserService;
import com.tripsok_back.type.LocaleCode;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TripPlanServiceImpl implements TripPlanService {
	private final TripPlanRepository tripPlanRepository;
	private final UserService userService;
	private final PlaceService placeService;

	public TripPlanServiceImpl(TripPlanRepository tripPlanRepository, UserService userService,
		@Qualifier("placeDefaultServiceImpl") PlaceService placeService) {
		this.tripPlanRepository = tripPlanRepository;
		this.userService = userService;
		this.placeService = placeService;
	}

	@Override
	@Transactional
	public TripPlanResponse createOrUpdateTripPlan(Integer userId, UpdateTripPlanRequest request, LocaleCode locale) {
		TripSokUser user = userService.findUserById(userId);
		TripPlan tripPlan = findTripPlanByUser(user);
		Set<VisitSpot> existingVisitSpots = getVisitSpotSet(request.visitSpotSet(), tripPlan);
		tripPlan.updateTripPlan(request, existingVisitSpots);
		return new TripPlanResponse(tripPlan, locale);
	}

	@Override
	@Transactional
	public TripPlanResponse getTripPlan(Integer userId, LocaleCode locale) {
		TripSokUser user = userService.findUserById(userId);
		TripPlan tripPlan = tripPlanRepository.findByUserIdAndStatus(user.getId(), TripPlan.PlanStatus.DRAFT);
		return tripPlan==null ? null : new TripPlanResponse(tripPlan, locale);
	}

	@Override
	public TripPlan findDraftTripPlanByUserId(Integer userId) {
		TripPlan tripPlan = tripPlanRepository.findByUserIdAndStatus(userId, TripPlan.PlanStatus.DRAFT);
		if (tripPlan == null) {
			throw new TripPlanException(ErrorCode.TRIP_PLAN_NOT_FOUND);
		}
		return tripPlan;
	}

	private Set<VisitSpot> getVisitSpotSet(Set<UpdateVisitSpotRequest> visitSpots, TripPlan tripPlan) {
		Set<Integer> placeIds = visitSpots.stream()
			.map(UpdateVisitSpotRequest::placeId).collect(Collectors.toSet());

		Map<Integer, Place> placeMap = placeService.findPlacesByIds(placeIds).stream()
			.collect(Collectors.toMap(Place::getId, Function.identity()));

		return visitSpots.stream()
			.map(visitSpot -> {
				Place place = placeMap.get(visitSpot.placeId());
				if (place == null) {
					log.warn("VisitSpot을 가져오는 중 존재하지 않는 장소를 참조하여 무시합니다. placeId: {}", visitSpot.placeId());
					return null;
				}
				return new VisitSpot(place, visitSpot.memo(), visitSpot.orderIndex(), tripPlan);
			})
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());
	}

	private TripPlan findTripPlanByUser(TripSokUser user) {
		TripPlan tripPlan = tripPlanRepository.findByUserIdAndStatus(user.getId(), TripPlan.PlanStatus.DRAFT);
		if (tripPlan == null) {
			tripPlan = new TripPlan(user);
			tripPlanRepository.save(tripPlan);
		}
		return tripPlan;
	}
}
