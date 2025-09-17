package com.tripsok_back.dto.tourApi;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
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