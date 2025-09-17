package com.tripsok_back.dto.tourApi;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class TourApiPlaceRequiredDto {

	@Builder.Default
	private final String mobileOS = "ETC";
	@Builder.Default
	private final String mobileApp = "Tourang";
	@Builder.Default
	private final String responseType = "json";
	private String serviceKey;
}
