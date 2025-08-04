package com.tripsok_back.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TouristItemRequestDto {
	private Integer numOfRows;
	private Integer pageNo;
	private String mobileOS;
	private String mobileApp;
	private String type;
	private Integer ContentTypeId;
	private String arrange;
	private String areaCode;
	private String serviceKey;
}
