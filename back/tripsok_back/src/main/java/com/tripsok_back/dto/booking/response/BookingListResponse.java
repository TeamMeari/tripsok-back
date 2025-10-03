package com.tripsok_back.dto.booking.response;

import java.util.Set;

import com.tripsok_back.model.booking.Booking;
import com.tripsok_back.type.LocaleCode;

public record BookingListResponse(
	Integer bookingId,
	String tripDate,
	String startTime,
	Integer numberOfPeople,
	LocaleCode locale,
	Booking.BookingStatus status,
	Set<GetBookingSpotSetResponse> BookingSpotSet) {
}
