package com.tripsok_back.dto.tourApi;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class TourApiPlaceRequestDto extends TourApiPlaceRequiredDto {
	private Integer numOfRows;
	private Integer pageNo;
	private Integer contentTypeId;
	private String arrange;
	private String areaCode;
}