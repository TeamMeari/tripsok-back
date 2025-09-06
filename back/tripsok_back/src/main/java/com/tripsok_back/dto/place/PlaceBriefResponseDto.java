package com.tripsok_back.dto.place;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.tripsok_back.model.place.Place;

public record PlaceBriefResponseDto(
	Integer id,
	String name,
	String address,
	String type,               // 필요시 enum 으로 교체
	BigDecimal lat,            // DB가 DECIMAL이면 유지, 아니면 Double 권장
	BigDecimal lng,
	Integer likeCount,
	Integer viewCount,
	Integer reviewCount,       // 사전 계산 값 사용 권장(아래 설명)
	String thumbnailUrl,       // 목록엔 보통 1장만
	Integer imageCount,        // 전체 이미지 수
	Instant updatedAt,
	Set<Integer> themes
) {

	public static PlaceBriefResponseDto from(
		Place p,
		String type,
		String thumbnailUrl,
		Integer imageCount,
		Integer reviewCount
	) {
		Objects.requireNonNull(p, "place must not be null");

		return new PlaceBriefResponseDto(
			p.getId(),
			p.getPlaceName(),
			p.getAddress(),
			type,
			p.getMapY(), // lat
			p.getMapX(), // lng
			p.getLike(),
			p.getView(),
			reviewCount != null ? reviewCount : 0,
			thumbnailUrl,
			imageCount != null ? imageCount : 0,
			p.getUpdatedAt().atZone(ZoneId.of("Asia/Seoul")).toInstant(),
			p.getThemes().stream().map(it-> it.getTheme().getId()).collect(Collectors.toSet())
		);
	}
}
