package com.tripsok_back.dto.place;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.tripsok_back.model.place.Place;
import com.tripsok_back.model.place.PlaceTr;
import com.tripsok_back.model.place.accommodation.Accommodation;
import com.tripsok_back.model.place.restaurant.Restaurant;
import com.tripsok_back.model.place.tour.Tour;
import com.tripsok_back.type.LocaleCode;
import com.tripsok_back.type.PlaceJoinType;

import lombok.Builder;

public record PlaceDetailResponseDto(
	Integer id, String language, String placeName, String summary, String address, String contact, String email,
	String information,
	Integer view, Integer like, BigDecimal mapX, BigDecimal mapY, LocalDateTime createdAt, LocalDateTime updatedAt,
	PlaceJoinType type,
	ChildSummary child
) {
	@Builder
	public PlaceDetailResponseDto {
	}

	public static PlaceDetailResponseDto from(Place place, PlaceJoinType type, LocaleCode locale) {
		if (place == null)
			return null;
		PlaceTr tr = null;
		if (locale != null)
			tr = place.getPlaceTr(locale);
		if (tr == null)
			tr = place.getPlaceTr(LocaleCode.KO);
		return PlaceDetailResponseDto.builder()
			.id(place.getId())
			.language(locale.getCode())
			.placeName(tr != null ? tr.getPlaceName() : null)
			.summary(tr != null ? tr.getSummary() : null)
			.address(tr != null ? tr.getAddress() : null)
			.contact(place.getContact())
			.email(place.getEmail())
			.information(tr != null ? tr.getInformation() : null)
			.view(place.getView())
			.like(place.getLike())
			.mapX(place.getMapX())
			.mapY(place.getMapY())
			.createdAt(place.getCreatedAt())
			.updatedAt(place.getUpdatedAt())
			.type(type)
			.child(switch (type) {
				case ACCOMMODATION -> {
					Accommodation a = place.getAccommodation();
					yield a == null ? null :
						new AccommodationSummary(a.getId(), a.getPlaceLclsCategory().getLclsSystm3Name(locale),
							a.getImageUrlList());
				}
				case RESTAURANT -> {
					Restaurant r = place.getRestaurant();
					yield r == null ? null :
						new RestaurantSummary(r.getId(), r.getPlaceLclsCategory().getLclsSystm3Name(locale),
							r.getImageUrlList());
				}
				case TOUR -> {
					Tour t = place.getTour();
					yield t == null ? null :
						new TourSummary(t.getId(), t.getPlaceLclsCategory().getLclsSystm3Name(locale),
							t.getImageUrlList());
				}
			})
			.build();
	}

	public sealed interface ChildSummary permits AccommodationSummary, RestaurantSummary, TourSummary {
	}

	public record AccommodationSummary(Integer id, String accommodationType, List<String> imageList)
		implements ChildSummary {
	}

	public record RestaurantSummary(Integer id, String restaurantType, List<String> imageList) implements ChildSummary {
	}

	public record TourSummary(Integer id, String tourType, List<String> imageList) implements ChildSummary {
	}
}
