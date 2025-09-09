package com.tripsok_back.dto.user.response;

import com.tripsok_back.type.LocaleCode;
import com.tripsok_back.type.PlaceJoinType;

import lombok.Builder;

@Builder
public record InterestPlaceResponse(Integer id, LocaleCode language, Integer placeId, String name, PlaceJoinType type, String thumbnailUrl) {
}
