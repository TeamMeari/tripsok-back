package com.tripsok_back.controller.place;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tripsok_back.dto.PageResponse;
import com.tripsok_back.dto.place.PlaceBriefResponseDto;
import com.tripsok_back.dto.place.PlaceDetailResponseDto;
import com.tripsok_back.dto.place.PlaceSortStyle;
import com.tripsok_back.exception.TourApiException;
import com.tripsok_back.service.place.PlaceService;
import com.tripsok_back.type.TourismType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Places", description = "장소(숙소/쇼핑/관광/축제) 목록 및 상세 조회 API")
public class PlaceController {

	private final List<PlaceService> placeService;

	private PlaceService getService(TourismType type) {
		return placeService.stream()
			.filter(p -> p.getType() == type)
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("No processor found for type: " + type));
	}

	@Operation(
		summary = "장소 목록 조회",
		description = """
			카테고리별 장소 목록을 페이지네이션과 정렬로 조회합니다.
			결과가 0건이어도 200 OK와 빈 content를 반환합니다.
			"""
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 카테고리 값 등 잘못된 요청", content = @Content),
		@ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
	})
	@GetMapping("/{category}")
	public ResponseEntity<PageResponse<PlaceBriefResponseDto>> getPlaceList(
		@Parameter(
			description = "카테고리",
			schema = @Schema(allowableValues = {"accommodation", "restaurant", "tour", "wrong-category"})
		)
		@PathVariable String category,

		@Parameter(description = "페이지 인덱스", example = "0",
			schema = @Schema(minimum = "0"))
		@RequestParam(defaultValue = "0") @Min(0) int page,

		@Parameter(description = "페이지 크기", example = "20",
			schema = @Schema(minimum = "10", maximum = "100"))
		@RequestParam(defaultValue = "20") @Min(10) @Max(100) int size,

		@Parameter(description = "정렬 스타일(쿼리 파라미터 집합)")
		@ParameterObject
		@RequestParam(required = false) PlaceSortStyle sortStyle
	) {
		Sort sort = (sortStyle != null)
			? sortStyle.toSort()
			: Sort.by(Sort.Order.desc("updatedAt"));

		Pageable pageable = PageRequest.of(page, size, sort);

		TourismType type = TourismType.fromOrThrow(category);
		log.info("{} 항목 리스트 조회 시작", type.name());
		PageResponse<PlaceBriefResponseDto> body = getService(type).getPlaceList(pageable);
		return ResponseEntity.ok(body);
	}

	@Operation(
		summary = "장소 상세 조회",
		description = """
			카테고리와 ID로 단건 상세 정보를 조회합니다.
			존재하지 않으면 404 Not Found를 반환합니다.
			"""
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "상세 조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 카테고리 값", content = @Content),
		@ApiResponse(responseCode = "404", description = "해당 리소스 없음", content = @Content),
		@ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
	})
	@GetMapping("/{category}/{id}")
	public ResponseEntity<PlaceDetailResponseDto> getPlaceDetail(
		@Parameter(
			description = "카테고리",
			schema = @Schema(allowableValues = {"accommodation", "restaurant", "tour", "wrong-category"})
		)
		@PathVariable String category,

		@Parameter(description = "리소스 ID", example = "123")
		@PathVariable int id
	) {
		TourismType type = TourismType.fromOrThrow(category);
		try {
			return ResponseEntity.of(getService(type).getPlaceDetail(id)); // empty → 404
		} catch (TourApiException e) {
			return ResponseEntity.notFound().build();
		}
	}
}
