package com.tripsok_back.dto.place;

import java.util.List;

import com.tripsok_back.model.place.Place;
import com.tripsok_back.model.place.PlaceLclsCategory;
import com.tripsok_back.model.place.PlaceLclsCategoryTr;
import com.tripsok_back.model.place.PlaceTr;
import com.tripsok_back.type.LocaleCode;
import com.tripsok_back.type.TourismType;
import com.tripsok_back.util.EmbeddingUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class PlaceDocument {
	private String id;
	private String placeId;
	private String locale;
	private String title;
	private String address;
	private String summary;
	private String information;
	private String thumbnailUrl;
	private Double lat;
	private Double lng;
	private GeoPoint location;
	private String type;
	private Integer like;
	private Integer view;
	private Long updatedAt;
	private List<String> categories;
	private List<String> tags;
	private List<String> themes;
	private List<Float> embedding;

	public static PlaceDocument fromEntity(Place place,
		LocaleCode localeCode,
		TourismType type,
		EmbeddingUtil embeddingUtil) {

		String esId = place.getId() + "-" + localeCode.getCode();

		PlaceTr placeTr = place.getPlaceTrs().stream()
			.filter(tr -> localeCode.getCode().equals(tr.getId().getLocale()))
			.findFirst()
			.orElse(null);

		String title = placeTr.getPlaceName();
		String address = placeTr.getAddress();
		String summary = placeTr.getSummary();
		String info = placeTr.getInformation();

		String categoryName = "";
		String thumb = null;
		Double lat = null;
		Double lng = null;
		GeoPoint geoPoint = null;

		if (place.getMapY() != null && place.getMapX() != null) {
			try {
				lat = place.getMapY().doubleValue();
				lng = place.getMapX().doubleValue();
				geoPoint = new GeoPoint(lat, lng);
			} catch (Exception ignored) {
			}
		}

		List<String> themes = List.of();
		switch (type) {
			case ACCOMMODATION -> {
				if (place.getAccommodation() == null) {
					log.info("placeId:{} 의 accommodation이 없습니다", place.getId());
					break;
				}
				categoryName = extractCategoryName(
					place.getAccommodation().getPlaceLclsCategory(), localeCode.getCode());
				thumb = extractThumbnail(place.getAccommodation().getImageUrlList());
			}
			case RESTAURANT -> {
				if (place.getRestaurant() == null) {
					log.info("placeId:{} 의 restaurant이 없습니다", place.getId());
					break;
				}
				categoryName = extractCategoryName(
					place.getRestaurant().getPlaceLclsCategory(), localeCode.getCode());
				thumb = extractThumbnail(place.getRestaurant().getImageUrlList());
			}
			case TOURIST_SPOT -> {
				if (place.getTour() == null) {
					log.info("placeId:{} 의 tour이 없습니다", place.getId());
					break;
				}
				categoryName = extractCategoryName(
					place.getTour().getPlaceLclsCategory(), localeCode.getCode());
				thumb = extractThumbnail(place.getTour().getImageUrlList());
			}
		}
		//TODO 태그랑 테마 추가
		PlaceDocument doc = PlaceDocument.builder()
			.id(esId)
			.placeId(java.lang.String.valueOf(place.getId()))
			.locale(localeCode.getCode())
			.categories(categoryName != null ? List.of(categoryName) : List.of())
			.tags(List.of())
			.themes(themes)
			.title(title)
			.address(address)
			.summary(summary)
			.information(info)
			.thumbnailUrl(thumb)
			.lat(lat)
			.lng(lng)
			.location(geoPoint)
			.type(type != null ? type.name() : null)
			.like(place.getLike())
			.view(place.getView())
			.updatedAt(place.getUpdatedAt() != null ? place.getUpdatedAt()
				.atZone(java.time.ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli() : null)
			.build();

		try {
			List<Float> vector = embeddingUtil.embed(title + " " + summary);
			doc.setEmbedding(vector);
		} catch (Exception e) {
			doc.setEmbedding(null);
		}
		return doc;
	}

	private static String extractCategoryName(PlaceLclsCategory category, String locale) {
		return category.getPlaceLclsCategoryTrs().stream()
			.filter(tr -> locale.equals(tr.getId().getLocale()))
			.map(PlaceLclsCategoryTr::getLclsSystm3Name)
			.findFirst()
			.orElse(null);
	}

	private static String extractThumbnail(List<String> urls) {
		if (urls != null && !urls.isEmpty()) {
			return urls.getFirst();
		}
		return null;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class GeoPoint {
		private double lat;
		private double lon;
	}

}
