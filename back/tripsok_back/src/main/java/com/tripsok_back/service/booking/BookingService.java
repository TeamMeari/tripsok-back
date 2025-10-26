package com.tripsok_back.service.booking;

import java.util.List;

import com.tripsok_back.dto.booking.request.BookingRequest;
import com.tripsok_back.dto.booking.request.BookingSpotMemoUpdateRequest;
import com.tripsok_back.dto.booking.response.BookingDetailResponse;
import com.tripsok_back.dto.booking.response.BookingResponse;
import com.tripsok_back.dto.booking.response.BookingShareResponse;
import com.tripsok_back.dto.booking.response.CompleteBookingResponse;
import com.tripsok_back.type.LocaleCode;

public interface BookingService {
	void validateBookingTripPlan(Integer userId);

	CompleteBookingResponse completeBooking(Integer userId, LocaleCode locale, BookingRequest request);

	void updateBookingSpotMemo(Integer userId, BookingSpotMemoUpdateRequest request);

	List<BookingResponse> getUserBookings(Integer userId);

	BookingDetailResponse getBookingDetail(Integer userId, Integer bookingId, String shareCode);

	BookingShareResponse getBookingShareInfo(Integer bookingId, Integer userId);

	BookingResponse getLatestBooking(Integer userId);
}
