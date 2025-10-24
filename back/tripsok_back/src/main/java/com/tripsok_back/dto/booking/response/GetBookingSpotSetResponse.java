package com.tripsok_back.dto.booking.response;

import com.fasterxml.jackson.annotation.JsonProperty;

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
	@Schema(description = "기존 mapY = lat (deprecated)", deprecated = true, example = "37.5665")
	@JsonProperty("latitude")
	public Double getMapY() {
		return lat.doubleValue();
	}

	@Schema(description = "기존 mapX = lng (deprecated)", deprecated = true, example = "126.9780")
	@JsonProperty("longitude")
	public Double getMapX() {
        return lng;
	}
}
