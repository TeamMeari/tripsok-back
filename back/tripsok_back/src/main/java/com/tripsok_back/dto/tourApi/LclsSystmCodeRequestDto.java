package com.tripsok_back.dto.tourApi;

import jakarta.validation.constraints.NotBlank;
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
public class LclsSystmCodeRequestDto {
	@NotBlank
	private String serviceKey;
	private String MobileOS = "ETC";
	private String MobileApp = "AppTest";
	private Integer numOfRows = 300;
	private Integer pageNo = 1;
	private String _type = "json";
	private String lclsSystm1;
	private String lclsSystm2;
	private String lclsSystm3;
	private String lclsSystmListYn = "Y";

}
