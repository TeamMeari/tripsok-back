package com.tripsok_back.controller;

import static com.tripsok_back.exception.ErrorCode.*;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tripsok_back.annotation.ApiErrorCodes;
import com.tripsok_back.dto.booking.request.BookingRequest;
import com.tripsok_back.dto.booking.request.BookingSpotMemoUpdateRequest;
import com.tripsok_back.dto.booking.response.BookingDetailResponse;
import com.tripsok_back.dto.booking.response.BookingResponse;
import com.tripsok_back.dto.booking.response.BookingShareResponse;
import com.tripsok_back.dto.booking.response.CompleteBookingResponse;
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
		NOT_FOUND_BOOKING_TRIP_PLAN,
		BOOKING_PAST_TRIP_DATE,
		INCOMPLETE_TRIP_PLAN
	})
	@GetMapping
	public ResponseEntity<Void> validateBooking(@AuthenticationPrincipal Integer userId) {
		bookingService.validateBookingTripPlan(userId);
		return ResponseEntity.noContent().build();
	}

	@Operation(
		summary = "예약 완료",
		description = "현재 인증된 사용자의 예약을 완료합니다."
	)
	@ApiErrorCodes({
		NOT_FOUND_BOOKING_TRIP_PLAN,
		EMAIL_SEND_FAILED,
		USER_NOT_FOUND
	})
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

	@Operation(
		summary = "여행지 메모 수정",
		description = "현재 인증된 사용자의 예약된 여행지 메모를 수정합니다."
	)
	@ApiErrorCodes({
		BOOKING_SPOT_NOT_FOUND,
		USER_NOT_FOUND
	})
	@PatchMapping("/spot-memo")
	public ResponseEntity<Void> UpdateBookingSpotMemo(
		@AuthenticationPrincipal Integer userId,
		@RequestBody
		@Parameter(description = "여행지 메모 수정 정보", required = true)
		@Valid
		BookingSpotMemoUpdateRequest request
	) {
		bookingService.updateBookingSpotMemo(userId, request);
		return ResponseEntity.noContent().build();
	}

	@Operation(
		summary = "사용자 예약 내역 조회",
		description = "현재 인증된 사용자의 모든 예약 내역을 조회합니다."
	)
	@GetMapping("/list")
	public ResponseEntity<List<BookingResponse>> getUserBookings(
		@AuthenticationPrincipal Integer userId
	) {
		return ResponseEntity.ok(bookingService.getUserBookings(userId));
	}

	@Operation(
		summary = "예약 상세 조회",
		description = "현재 인증된 사용자의 특정 예약 내역 상세 정보를 조회합니다."
	)
	@ApiErrorCodes({
		BOOKING_NOT_FOUND,
		BOOKING_USER_MISMATCH
	})
	@GetMapping("/detail/{bookingId}")
	public ResponseEntity<BookingDetailResponse> getBookingDetail(
		@AuthenticationPrincipal Integer userId,
		@Parameter(description = "예약 번호", required = true)
		@PathVariable(name = "bookingId") Integer bookingId,
		@Parameter(description = "공유 코드")
		@RequestParam(name = "shareCode", required = false) String shareCode
	) {
		return ResponseEntity.ok(bookingService.getBookingDetail(userId, bookingId, shareCode));
	}

	@Operation(
		summary = "예약 내역 공유 코드 생성 및 조회",
		description = "현재 인증된 사용자의 특정 예약 내역에 대한 공유 코드를 생성하거나 조회합니다."
	)
	@ApiErrorCodes({
		BOOKING_NOT_FOUND,
		BOOKING_USER_MISMATCH
	})
	@GetMapping("/share/{bookingId}/code")
	public ResponseEntity<BookingShareResponse> getBookingShareCode(
		@AuthenticationPrincipal Integer userId,
		@Parameter(description = "예약 번호", required = true)
		@PathVariable(name = "bookingId") Integer bookingId
	) {
		return ResponseEntity.ok(bookingService.getBookingShareInfo(bookingId, userId));
	}
}
