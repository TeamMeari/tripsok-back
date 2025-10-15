package com.tripsok_back.dto.booking.response;

public record GetBookingSpotSetResponse(
	Integer placeId,
	String placeName,
	String address,
	Double latitude,
	Double longitude,
	Integer orderIndex,
	String memo
) {
}
