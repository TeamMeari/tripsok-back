package com.tripsok_back.dto.tourApi;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TourApiPlaceRequestDto {
	private Integer numOfRows;
	private Integer pageNo;
	private String mobileOS;
	private String mobileApp;
	private String type;
	private Integer contentTypeId;
	private String arrange;
	private String areaCode;
	private String serviceKey;
}
