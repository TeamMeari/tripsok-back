package com.tripsok_back.dto.tourApi;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class LclsSystmCodeRequestDto extends TourApiPlaceRequiredDto{

	@Builder.Default
	private Integer numOfRows = 300;
	@Builder.Default
	private Integer pageNo = 1;
	private String lclsSystm1;
	private String lclsSystm2;
	private String lclsSystm3;
	@Builder.Default
	private String lclsSystmListYn = "Y";

}
