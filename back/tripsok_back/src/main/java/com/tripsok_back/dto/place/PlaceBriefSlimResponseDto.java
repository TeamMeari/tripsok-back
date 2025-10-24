package com.tripsok_back.dto.place;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;

import com.tripsok_back.model.place.Place;
import com.tripsok_back.model.place.PlaceTr;
import com.tripsok_back.type.LocaleCode;

public record PlaceBriefSlimResponseDto(
	Integer id,
	String language,
	String name,
	String summary,
	String type,
	Double lat,
	Double lng,
	String thumbnailUrl,
	Instant updatedAt
) {

	public static PlaceBriefSlimResponseDto from(
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

		return new PlaceBriefSlimResponseDto(
			p.getId(),
			locale != null ? locale.getLanguage() : null,
			tr != null ? tr.getPlaceName() : null,
			tr.getSummary(),
			type,
			p.getMapY().doubleValue(), // lat
			p.getMapX().doubleValue(), // lng
			thumbnailUrl,
			p.getUpdatedAt() != null ? p.getUpdatedAt().atZone(ZoneId.of("Asia/Seoul")).toInstant() : null
		);
	}

	public static PlaceBriefSlimResponseDto from(Place p, LocaleCode locale) {
		if (p == null || locale == null)
			return null;
		PlaceTr tr = p.getPlaceTr(locale);
		if (tr == null)
			return null;

		String type = p.getTour() != null ? com.tripsok_back.type.TourismType.TOURIST_SPOT.name()
			: p.getRestaurant() != null ? com.tripsok_back.type.TourismType.RESTAURANT.name()
			: com.tripsok_back.type.TourismType.ACCOMMODATION.name();

		String thumb = null;
		try {
			if (p.getTour() != null) {
				java.util.List<String> urls = p.getTour().getImageUrlList();
				thumb = (urls != null && !urls.isEmpty()) ? urls.get(0) : null;
			} else if (p.getRestaurant() != null) {
				java.util.List<String> urls = p.getRestaurant().getImageUrlList();
				thumb = (urls != null && !urls.isEmpty()) ? urls.get(0) : null;
			} else if (p.getAccommodation() != null) {
				java.util.List<String> urls = p.getAccommodation().getImageUrlList();
				thumb = (urls != null && !urls.isEmpty()) ? urls.get(0) : null;
			}
		} catch (Exception ignored) {
		}

		return new PlaceBriefSlimResponseDto(
			p.getId(),
			locale.getCode(),
			tr.getPlaceName(),
			tr.getSummary(),
			type,
			p.getMapY().doubleValue(),
			p.getMapX().doubleValue(),
			thumb,
			p.getUpdatedAt().atZone(ZoneId.of("Asia/Seoul")).toInstant()
		);
	}

	public static PlaceBriefSlimResponseDto from(PlaceDocument d) {
		if (d == null)
			return null;
		Integer id = null;
		try {
			id = Integer.valueOf(d.getPlaceId());
		} catch (Exception ignored) {
		}
		return new PlaceBriefSlimResponseDto(
			id,
			d.getLocale(),
			d.getTitle(),
			d.getSummary(),
			d.getType(),
			d.getLat() != null ? Double.valueOf(d.getLat()) : null,
			d.getLng() != null ? Double.valueOf(d.getLng()) : null,
			d.getThumbnailUrl(),
			d.getUpdatedAt() != null ? Instant.ofEpochMilli(d.getUpdatedAt()) : null
		);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		PlaceBriefSlimResponseDto other = (PlaceBriefSlimResponseDto)obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
