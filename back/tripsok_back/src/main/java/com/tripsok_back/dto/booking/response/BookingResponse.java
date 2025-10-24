package com.tripsok_back.dto.booking.response;

import java.time.LocalDate;
import java.time.LocalTime;

import com.tripsok_back.model.booking.Booking;
import com.tripsok_back.model.booking.Booking.BookingStatus;

public record BookingResponse(
	Integer bookingId,
	LocalDate tripDate,
	LocalTime startTime,
	Integer numberOfPeople,
	BookingStatus status) {
	public BookingResponse(Booking booking) {
		this(
			booking.getId(),
			booking.getTripDate(),
			booking.getStartTime(),
			booking.getNumberOfPeople(),
			booking.getCurrentStatus()
		);
	}
}
