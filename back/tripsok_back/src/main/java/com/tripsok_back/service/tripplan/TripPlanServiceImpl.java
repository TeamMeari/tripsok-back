package com.tripsok_back.service.tripplan;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tripsok_back.dto.tripplan.TripPlanCommonDto;
import com.tripsok_back.dto.tripplan.request.UpdateTripPlanRequest;
import com.tripsok_back.dto.tripplan.request.UpdateVisitSpotRequest;
import com.tripsok_back.dto.tripplan.response.TripPlanResponse;
import com.tripsok_back.dto.tripplan.response.VisitSpotResponse;
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
		TripPlan tripPlan = tripPlanRepository.findByUserId(userId).orElse(new TripPlan(user));
		Set<VisitSpot> existingVisitSpots = getVisitSpotSet(request.visitSpotSet(), tripPlan);
		tripPlan.updateTripPlan(request, existingVisitSpots);
		return convertToTripPlanResponse(tripPlan, locale);
	}

	@Override
	@Transactional
	public TripPlanResponse getTripPlan(Integer userId, LocaleCode locale) {
		TripSokUser user = userService.findUserById(userId);
		TripPlan tripPlan = tripPlanRepository.findByUserId(userId).orElse(new TripPlan(user));
		return convertToTripPlanResponse(tripPlan, locale);
	}

	private Set<VisitSpot> getVisitSpotSet(Set<UpdateVisitSpotRequest> visitSpots, TripPlan tripPlan) {
		return visitSpots.stream()
			.map(visitSpot -> {
				try {
					Place place = placeService.findPlaceById(visitSpot.placeId());
					return new VisitSpot(place, visitSpot.memo(), visitSpot.orderIndex(), tripPlan);
				} catch (Exception e) {
					log.warn("VisitSpot을 가져오는 중 존재하지 않는 장소를 참조하여 무시합니다. placeId: {}", visitSpot.placeId());
					return null;
				}
			})
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());
	}

	private TripPlanResponse convertToTripPlanResponse(TripPlan tripPlan, LocaleCode locale) {
		Set<VisitSpot> sortedVisitSpots = tripPlan.getVisitSpotSet().stream()
			.sorted(Comparator.comparingInt(VisitSpot::getOrderIndex))
			.collect(Collectors.toCollection(LinkedHashSet::new));
		Set<VisitSpotResponse> visitSpotResponses = sortedVisitSpots.stream()
			.map(it -> new VisitSpotResponse(it, locale))
			.collect(Collectors.toCollection(LinkedHashSet::new));
		return new TripPlanResponse(new TripPlanCommonDto(tripPlan), visitSpotResponses);
	}
}
