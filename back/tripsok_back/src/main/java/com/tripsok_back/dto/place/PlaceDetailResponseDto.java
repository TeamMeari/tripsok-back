package com.tripsok_back.dto.place;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.tripsok_back.model.place.Place;
import com.tripsok_back.model.place.accommodation.Accommodation;
import com.tripsok_back.model.place.restaurant.Restaurant;
import com.tripsok_back.model.place.tour.Tour;
import com.tripsok_back.type.PlaceJoinType;

import lombok.Builder;

public record PlaceDetailResponseDto(
	Integer id, String placeName, String address, String contact, String email,
	String information,
	Integer view, Integer like, BigDecimal mapX, BigDecimal mapY, LocalDateTime createdAt, LocalDateTime updatedAt,
	PlaceJoinType type,
	Set<PlaceTagResponseDto> tags,
	ChildSummary child
) {
	@Builder
	public PlaceDetailResponseDto {
	}

	public static PlaceDetailResponseDto from(Place place, PlaceJoinType type) {
		if (place == null)
			return null;
		return PlaceDetailResponseDto.builder()
			.id(place.getId())
			.placeName(place.getPlaceName())
			.address(place.getAddress())
			.contact(place.getContact())
			.email(place.getEmail())
			.information(place.getInformation())
			.view(place.getView())
			.like(place.getLike())
			.mapX(place.getMapX())
			.mapY(place.getMapY())
			.createdAt(place.getCreatedAt())
			.updatedAt(place.getUpdatedAt())
			.type(type)
			.tags(place.getTags().stream().map(pt -> new PlaceTagResponseDto(pt.getTag().getId(), pt.getTag().getName())).collect(
				Collectors.toSet()))
			.child(switch (type) {
				case ACCOMMODATION -> {
					Accommodation a = place.getAccommodation();
					yield a == null ? null :
						new AccommodationSummary(a.getId(), a.getAccommodationType(), a.getImageUrlList());
				}
				case RESTAURANT -> {
					Restaurant r = place.getRestaurant();
					yield r == null ? null :
						new RestaurantSummary(r.getId(), r.getRestaurantType(), r.getImageUrlList());
				}
				case TOUR -> {
					Tour t = place.getTour();
					yield t == null ? null : new TourSummary(t.getId(), t.getTourType(), t.getImageUrlList());
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
