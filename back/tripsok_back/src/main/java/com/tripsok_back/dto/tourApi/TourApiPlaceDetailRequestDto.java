package com.tripsok_back.dto.tourApi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class TourApiPlaceDetailRequestDto extends TourApiPlaceRequiredDto{

	private Integer contentId;
}
