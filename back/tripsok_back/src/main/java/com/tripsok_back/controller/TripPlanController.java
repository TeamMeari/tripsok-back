package com.tripsok_back.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tripsok_back.dto.tripplan.request.UpdateTripPlanRequest;
import com.tripsok_back.dto.tripplan.response.TripPlanResponse;
import com.tripsok_back.service.tripplan.TripPlanService;
import com.tripsok_back.type.LocaleCode;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/trip-plan")
@RequiredArgsConstructor
@Slf4j
@Validated
public class TripPlanController {
	private final TripPlanService tripPlanService;

	@PostMapping
	public ResponseEntity<TripPlanResponse> createTrip(
		@RequestBody
		@Valid UpdateTripPlanRequest request,
		@AuthenticationPrincipal Integer userId,
		@Parameter(description = "언어(로케일) 코드", example = "KO", schema = @Schema(type = "string", allowableValues = {
			"KO", "EN", "JA", "CN"}))
		@RequestParam(name = "locale", defaultValue = "KO") LocaleCode locale) {
		return ResponseEntity.ok(tripPlanService.createOrUpdateTripPlan(userId, request, locale));
	}

	@GetMapping
	public ResponseEntity<TripPlanResponse> getTrip(@AuthenticationPrincipal Integer userId,
		@Parameter(description = "언어(로케일) 코드", example = "KO", schema = @Schema(type = "string", allowableValues = {
			"KO", "EN", "JA", "CN"}))
		@RequestParam(name = "locale", defaultValue = "KO") LocaleCode locale) {
		return ResponseEntity.ok(tripPlanService.getTripPlan(userId, locale));
	}
}
