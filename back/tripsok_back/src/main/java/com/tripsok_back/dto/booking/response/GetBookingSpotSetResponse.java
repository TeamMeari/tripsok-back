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
    @Schema(description = "기존 latitude 필드입니다. `lat` 필드를 사용해주세요. (deprecated)", deprecated = true, example = "37.5665")
	@JsonProperty("latitude")
	public Double getMapY() {
		return lat.doubleValue();
	}

    @Schema(description = "기존 longitude 필드입니다. `lng` 필드를 사용해주세요. (deprecated)", deprecated = true, example = "126.9780")
	@JsonProperty("longitude")
	public Double getMapX() {
        return lng;
	}
}
