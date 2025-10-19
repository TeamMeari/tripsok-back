package com.tripsok_back.dto.tourApi;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class TourApiIntroRequestDto extends TourApiPlaceRequiredDto {
    private Integer numOfRows;
    private Integer pageNo;
    private Integer contentId;
    private Integer contentTypeId;
}

