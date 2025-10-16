package com.tripsok_back.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tripsok_back.annotation.ApiErrorCodes;
import com.tripsok_back.dto.booking.request.BookingRequest;
import com.tripsok_back.dto.booking.request.BookingSpotMemoUpdateRequest;
import com.tripsok_back.dto.booking.response.CompleteBookingResponse;
import com.tripsok_back.exception.ErrorCode;
import com.tripsok_back.service.booking.BookingService;
import com.tripsok_back.type.LocaleCode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/booking")
public class BookingController {
	private final BookingService bookingService;

	@Operation(
		summary = "예약할 여행계획 유효성 검증",
		description = "현재 인증된 사용자의 userId로 예약 가능한 여행계획인지 검증합니다. 결과는 반환하지 않고 204 No Content 응답만 제공합니다."
	)
	@ApiErrorCodes({
		ErrorCode.NOT_FOUND_BOOKING_TRIP_PLAN,
		ErrorCode.BOOKING_PAST_TRIP_DATE,
		ErrorCode.INCOMPLETE_TRIP_PLAN
	})
	@GetMapping
	public ResponseEntity<Void> validateBooking(@AuthenticationPrincipal Integer userId) {
		bookingService.validateBookingTripPlan(userId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping
	public ResponseEntity<CompleteBookingResponse> CompleteBooking(
		@RequestBody @Valid BookingRequest request,
		@AuthenticationPrincipal Integer userId,
		@Parameter(description = "언어(로케일) 코드", example = "KO", schema = @Schema(type = "string", allowableValues = {
			"KO", "EN", "JA", "CN"}))
		@RequestParam(name = "locale", defaultValue = "KO")
		LocaleCode locale
	) {
		return ResponseEntity.ok(bookingService.completeBooking(userId, locale, request));
	}

	@PatchMapping("/spot-memo")
	public ResponseEntity<Void> UpdateBookingSpotMemo(
		@AuthenticationPrincipal Integer userId,
		@RequestBody
		@Parameter(description = "여행지 메모 수정 정보", required = true)
		@Valid
		BookingSpotMemoUpdateRequest request
	) {
		bookingService.UpdateBookingSpotMemo(userId, request);
		return ResponseEntity.noContent().build();
	}

	/*
	@GetMapping
	public ResponseEntity<BookingListResponse> getBooking (
		@AuthenticationPrincipal Integer userId
		) {
		CompleteBookingResponse response = bookingService.getBooking(userId);
		return ResponseEntity.ok(response);
	}*/
}
