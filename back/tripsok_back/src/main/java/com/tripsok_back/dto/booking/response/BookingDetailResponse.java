package com.tripsok_back.dto.booking.response;

import java.util.Set;
import java.util.stream.Collectors;

import com.tripsok_back.model.booking.Booking;

public record BookingDetailResponse(
	Integer bookingId,
	String tripDate,
	String startTime,
	Integer numberOfPeople,
	Booking.BookingStatus status,
	Set<GetBookingSpotSetResponse> bookingSpotSet) {
	public BookingDetailResponse(Booking booking) {
		this(
			booking.getId(),
			booking.getTripDate().toString(),
			booking.getStartTime().toString(),
			booking.getNumberOfPeople(),
			booking.getCurrentStatus(),
			booking.getBookingSpotSet().stream()
				.map(GetBookingSpotSetResponse::new).collect(Collectors.toSet())
		);
	}
}
