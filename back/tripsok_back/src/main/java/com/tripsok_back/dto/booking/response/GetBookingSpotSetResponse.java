package com.tripsok_back.dto.booking.response;

import com.tripsok_back.model.booking.BookingSpot;

public record GetBookingSpotSetResponse(
	Integer placeId,
	String placeName,
	String address,
	Double latitude,
	Double longitude,
	Integer orderIndex,
	String memo
) {
	public GetBookingSpotSetResponse(BookingSpot bookingSpotSet) {
		this(
			bookingSpotSet.getPlaceId(),
			bookingSpotSet.getPlaceName(),
			bookingSpotSet.getAddress(),
			bookingSpotSet.getLatitude().doubleValue(),
			bookingSpotSet.getLongitude().doubleValue(),
			bookingSpotSet.getOrderIndex(),
			bookingSpotSet.getMemo()
		);
	}
}
