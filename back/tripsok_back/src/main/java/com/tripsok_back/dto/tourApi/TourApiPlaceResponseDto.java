package com.tripsok_back.dto.tourApi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
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
public class TourApiPlaceResponseDto {

	@NotNull
	@JsonProperty("contentid")
	private Integer contentId;

	@NotNull
	@JsonProperty("contenttypeid")
	private String contentTypeId;

	@NotNull
	@JsonProperty("title")
	private String title;

	@NotNull
	@JsonProperty("createdtime")
	private String createdTime;

	@NotNull
	@JsonProperty("modifiedtime")
	private String modifiedTime;

	@JsonProperty("firstimage")
	private String mainImageUrl;

	@JsonProperty("firstimage2")
	private String thumbnailImageUrl;

	@JsonProperty("addr1")
	private String address;

	@JsonProperty("addr2")
	private String addressDetail;

	@JsonProperty("areacode")
	private String areaCode;

	@JsonProperty("sigungucode")
	private String districtCode;

	@JsonProperty("zipcode")
	private String zipCode;

	@JsonProperty("tel")
	private String phoneNumber;

	@JsonProperty("mapy")
	private String latitude;

	@JsonProperty("mapx")
	private String longitude;

	@JsonProperty("mlevel")
	private String mapZoomLevel;

	@JsonProperty("cpyrhtDivCd")
	private String copyrightType;

	@JsonProperty("cat1")
	private String categoryMainCode;

	@JsonProperty("cat2")
	private String categoryMiddleCode;

	@JsonProperty("cat3")
	private String categorySubCode;

	@JsonProperty("lDongRegnCd")
	private String legalDongSidoCode;

	@JsonProperty("lDongSignguCd")
	private String legalDongSigunguCode;

	@JsonProperty("lclsSystm1")
	private String classificationMain;

	@JsonProperty("lclsSystm2")
	private String classificationMiddle;

	@JsonProperty("lclsSystm3")
	private String classificationSub;
}
