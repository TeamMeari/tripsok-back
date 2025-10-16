package com.tripsok_back.dto.tourApi;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class TourApiPlaceDetailRequestDto extends TourApiPlaceRequiredDto {

	private Integer contentId;
}
