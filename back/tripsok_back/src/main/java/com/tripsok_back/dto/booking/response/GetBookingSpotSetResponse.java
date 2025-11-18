package com.tripsok_back.dto.booking.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tripsok_back.model.booking.BookingSpot;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetBookingSpotSetResponse(
	Integer placeId,
	String placeName,
	String address,
	@Schema(description = "위도", example = "37.5759")
	Double lat,
	@Schema(description = "경도", example = "126.9768")
	Double lng,
	Integer orderIndex,
	String memo
) {
    @Schema(description = "기존 latitude 필드입니다. `lat` 필드를 사용해주세요. (deprecated)", deprecated = true, example = "37.5665")
	@JsonProperty("latitude")
	@Deprecated
	public Double getLatitudeDeprecated() {
		return lat;
	}

	@Schema(description = "기존 longitude 필드입니다. `lng` 필드를 사용해주세요. (deprecated)", deprecated = true, example = "126.9780")
	@JsonProperty("longitude")
	public Double getMapX() {
		return lng;
	}
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
