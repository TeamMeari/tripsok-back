package com.tripsok_back.service.booking;

import com.tripsok_back.dto.booking.request.BookingRequest;
import com.tripsok_back.dto.booking.request.BookingSpotMemoUpdateRequest;
import com.tripsok_back.dto.booking.response.CompleteBookingResponse;
import com.tripsok_back.type.LocaleCode;

public interface BookingService {
	void validateBookingTripPlan(Integer userId);
	CompleteBookingResponse completeBooking(Integer userId, LocaleCode locale, BookingRequest request);

	void UpdateBookingSpotMemo(Integer userId, BookingSpotMemoUpdateRequest request);
}
