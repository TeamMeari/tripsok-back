package com.tripsok_back.dto.place;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.tripsok_back.model.place.Place;
import com.tripsok_back.model.place.PlaceTr;
import com.tripsok_back.type.LocaleCode;

public record PlaceBriefResponseDto(
	Integer id,
	String language,
	String name,
	String address,
	String summary,
	String information,
	String type,
	Double lat,
	Double lng,
	Integer likeCount,
	Integer viewCount,
	Integer reviewCount,
	String thumbnailUrl,
	Integer imageCount,
	Instant updatedAt,
	Set<Integer> themes
) {

	public static PlaceBriefResponseDto from(
		Place p,
		String type,
		String thumbnailUrl,
		Integer imageCount,
		Integer reviewCount,
		LocaleCode locale
	) {
		Objects.requireNonNull(p, "place must not be null");

		PlaceTr tr = null;
		if (locale != null)
			tr = p.getPlaceTr(locale);
		if (tr == null)
			tr = p.getPlaceTr(LocaleCode.KO);

		return new PlaceBriefResponseDto(
			p.getId(),
			locale.getCode(),
			tr.getPlaceName(),
			tr.getAddress(),
			tr.getSummary(),
			tr.getInformation(),
			type,
			p.getMapY().doubleValue(), // lat
			p.getMapX().doubleValue(), // lng
			p.getLike(),
			p.getView(),
			reviewCount != null ? reviewCount : 0,
			thumbnailUrl,
			imageCount != null ? imageCount : 0,
			p.getUpdatedAt().atZone(ZoneId.of("Asia/Seoul")).toInstant(),
			p.getThemes().stream().map(it -> it.getTheme().getId()).collect(Collectors.toSet())
		);
	}

	public static PlaceBriefResponseDto from(Place p, LocaleCode locale) {
		if (p == null || locale == null)
			return null;
		PlaceTr tr = p.getPlaceTr(locale);
		if (tr == null)
			return null;

		String type = p.getTour() != null ? com.tripsok_back.type.TourismType.TOURIST_SPOT.name()
			: p.getRestaurant() != null ? com.tripsok_back.type.TourismType.RESTAURANT.name()
			: com.tripsok_back.type.TourismType.ACCOMMODATION.name();

		String thumb = null;
		Integer imageCount = 0;
		Integer reviewCount = 0;
		try {
			if (p.getTour() != null) {
				java.util.List<String> urls = p.getTour().getImageUrlList();
				thumb = (urls != null && !urls.isEmpty()) ? urls.get(0) : null;
				imageCount = p.getTour().getTourImages() != null ? p.getTour().getTourImages().size() : 0;
				reviewCount = p.getTour().getTourReviews() != null ? p.getTour().getTourReviews().size() : 0;
			} else if (p.getRestaurant() != null) {
				java.util.List<String> urls = p.getRestaurant().getImageUrlList();
				thumb = (urls != null && !urls.isEmpty()) ? urls.get(0) : null;
				imageCount =
					p.getRestaurant().getRestaurantImages() != null ? p.getRestaurant().getRestaurantImages().size() :
						0;
				reviewCount =
					p.getRestaurant().getRestaurantReviews() != null ? p.getRestaurant().getRestaurantReviews().size() :
						0;
			} else if (p.getAccommodation() != null) {
				java.util.List<String> urls = p.getAccommodation().getImageUrlList();
				thumb = (urls != null && !urls.isEmpty()) ? urls.get(0) : null;
				imageCount = p.getAccommodation().getAccommodationImages() != null ?
					p.getAccommodation().getAccommodationImages().size() : 0;
				reviewCount = p.getAccommodation().getAccommodationReviews() != null ?
					p.getAccommodation().getAccommodationReviews().size() : 0;
			}
		} catch (Exception ignored) {
		}

		return new PlaceBriefResponseDto(
			p.getId(),
			locale.getCode(),
			tr.getPlaceName(),
			tr.getAddress(),
			tr.getSummary(),
			tr.getInformation(),
			type,
			p.getMapY().doubleValue(),
			p.getMapX().doubleValue(),
			p.getLike(),
			p.getView(),
			reviewCount != null ? reviewCount : 0,
			thumb,
			imageCount != null ? imageCount : 0,
			p.getUpdatedAt().atZone(ZoneId.of("Asia/Seoul")).toInstant(),
			p.getThemes().stream().map(it -> it.getTheme().getId()).collect(Collectors.toSet())
		);
	}

	public static PlaceBriefResponseDto from(PlaceDocument d) {
		if (d == null)
			return null;
		Integer id = null;
		try {
			id = Integer.valueOf(d.getPlaceId());
		} catch (Exception ignored) {
		}
		return new PlaceBriefResponseDto(
			id,
			d.getLocale(),
			d.getTitle(),
			d.getAddress(),
			d.getSummary(),
			d.getInformation(),
			d.getType(),
			d.getLat() != null ? d.getLat() : null,
			d.getLng() != null ? d.getLng() : null,
			d.getLike() != null ? d.getLike() : 0,
			d.getView() != null ? d.getView() : 0,
			0,
			d.getThumbnailUrl(),
			0,
			d.getUpdatedAt() != null ? java.time.Instant.ofEpochMilli(d.getUpdatedAt()) : null,
			java.util.Collections.emptySet()
		);
	}
}
