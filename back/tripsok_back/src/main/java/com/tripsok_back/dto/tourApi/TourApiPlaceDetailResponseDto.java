package com.tripsok_back.dto.tourApi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TourApiPlaceDetailResponseDto {

	@JsonProperty("contentid")
	private String contentId;

	@JsonProperty("contenttypeid")
	private String contentTypeId;

	@JsonProperty("title")
	private String title;

	@JsonProperty("tel")
	private String telephone;

	@JsonProperty("telname")
	private String telephoneName;

	@JsonProperty("homepage")
	private String homepage;

	@JsonProperty("firstimage")
	private String firstImageUrl;

	@JsonProperty("firstimage2")
	private String firstImageUrlSecondary;

	@JsonProperty("cpyrhtDivCd")
	private String copyrightDivisionCode;

	@JsonProperty("areacode")
	private String areaCode;

	@JsonProperty("sigungucode")
	private String sigunguCode;

	@JsonProperty("lDongRegnCd")
	private String legalDongRegionCode;

	@JsonProperty("lDongSignguCd")
	private String legalDongSignguCode;

	@JsonProperty("lclsSystm1")
	private String largeClassificationSystem1;

	@JsonProperty("lclsSystm2")
	private String largeClassificationSystem2;

	@JsonProperty("lclsSystm3")
	private String largeClassificationSystem3;

	@JsonProperty("cat1")
	private String categoryLevel1;

	@JsonProperty("cat2")
	private String categoryLevel2;

	@JsonProperty("cat3")
	private String categoryLevel3;

	@JsonProperty("addr1")
	private String address1;

	@JsonProperty("addr2")
	private String address2;

	@JsonProperty("zipcode")
	private String zipcode;

	@JsonProperty("mapx")
	private String mapX;

	@JsonProperty("mapy")
	private String mapY;

	@JsonProperty("mlevel")
	private String mapLevel;

	@JsonProperty("overview")
	private String overview;

	@JsonProperty("createdtime")
	private String createdTime;

	@JsonProperty("modifiedtime")
	private String modifiedTime;
}