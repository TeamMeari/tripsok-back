package com.tripsok_back.dto.tourApi;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 요청 DTO
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LclsSystmCodeRequestDto {
	// 필수
	@NotBlank
	private String serviceKey;
	@Builder.Default
	private String MobileOS = "ETC";
	@Builder.Default
	private String MobileApp = "AppTest";
	// 선택
	@Builder.Default
	private Integer numOfRows = 300;
	@Builder.Default
	private Integer pageNo = 1;
	@Builder.Default
	private String _type = "json";
	private String lclsSystm1;
	private String lclsSystm2;
	private String lclsSystm3;
	@Builder.Default
	private String lclsSystmListYn = "Y";

}
