package com.tripsok_back.service.place;

import java.util.Set;

import com.tripsok_back.dto.place.PlaceTagResponseDto;
import com.tripsok_back.model.place.Place;
import com.tripsok_back.type.LocaleCode;

public interface TagService {
	Set<PlaceTagResponseDto> getPlaceTags(Place place, LocaleCode locale);
}
