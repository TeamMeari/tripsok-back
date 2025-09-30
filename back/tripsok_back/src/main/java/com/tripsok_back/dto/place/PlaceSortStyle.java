package com.tripsok_back.dto.place;

import org.springframework.data.domain.Sort;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "PlaceSortStyle", description = "장소 목록 정렬 옵션")
public class PlaceSortStyle {

	@Schema(
		description = "정렬 기준 컬럼명",
		example = "updatedAt",
		allowableValues = {"updatedAt", "view", "like","name"},
		defaultValue = "updatedAt"
	)
	private String sortKey = "updatedAt";

	@Schema(
		description = "정렬 방향",
		example = "desc",
		allowableValues = {"asc", "desc"},
		defaultValue = "desc"
	)
	private String direction = "desc";

	public Sort toSortEs() {
		String safeKey = switch (sortKey) {
			case "updatedAt", "view", "like", "name"-> sortKey;
			default -> "updatedAt";
		};

		Sort.Direction dir;
		try {
			dir = Sort.Direction.fromString(direction);
		} catch (IllegalArgumentException e) {
			dir = Sort.Direction.DESC;
		}
		return Sort.by(new Sort.Order(dir, safeKey));
	}

	public Sort toSortJpa() {
		String safeKey = switch (sortKey) {
			case "updatedAt", "view", "like" -> sortKey;
			case "name" -> sortKey = "placeTrs.placeName";
			default -> "updatedAt";
		};

		Sort.Direction dir;
		try {
			dir = Sort.Direction.fromString(direction);
		} catch (IllegalArgumentException e) {
			dir = Sort.Direction.DESC;
		}
		return Sort.by(new Sort.Order(dir, safeKey));
	}
}
