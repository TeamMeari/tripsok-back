package com.tripsok_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourApiPlaceDetailRequestDto {

	private String mobileOS;
	private String mobileApp;
	private String responseType;
	private String contentId;
	private String serviceKey;
}
