package com.tripsok_back.controller.place;

import java.util.ArrayList;
import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tripsok_back.dto.PageResponse;
import com.tripsok_back.dto.place.PlaceBriefSlimResponseDto;
import com.tripsok_back.dto.place.PlaceDetailResponseDto;
import com.tripsok_back.dto.place.PlaceSortStyle;
import com.tripsok_back.exception.TourApiException;
import com.tripsok_back.service.place.PlaceService;
import com.tripsok_back.service.search.PlaceEsService;
import com.tripsok_back.type.LocaleCode;
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
	private final PlaceEsService placeEsService;

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
			검색어(q)가 있으면 텍스트 검색 후 결과가 부족하면 임베딩 검색으로 보강합니다.
			결과가 0건이어도 200 OK와 빈 content를 반환합니다.
			"""
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 카테고리 값 등 잘못된 요청", content = @Content),
		@ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
	})
	@GetMapping("/{category}")
	public ResponseEntity<PageResponse<PlaceBriefSlimResponseDto>> getPlaceList(
		@AuthenticationPrincipal Integer userId,
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
		@ModelAttribute PlaceSortStyle sortStyle,

		@Parameter(description = "언어(로케일) 코드", example = "ko",
			schema = @Schema(allowableValues = {"ko", "en", "ja", "cn"}))
		@RequestParam(name = "locale", defaultValue = "ko") String locale,

		@Parameter(description = "테마 ID")
		@RequestParam(required = false) Integer themeId,

		@Parameter(description = "검색기능 사용시 카테고리 필터 사용 여부 (false면 전체 카테고리 검색)", example = "false")
		@RequestParam(name = "categoryFilter", required = false, defaultValue = "true") boolean categoryFilter,

		@Parameter(description = "통합 검색어(있으면 텍스트+임베딩 보강 검색 수행)")
		@RequestParam(required = false) String q
	) {
		Sort sortEs = (sortStyle != null)
			? sortStyle.toSortEs()
			: Sort.by(Sort.Order.desc("updatedAt"));
		Sort sortJpa = (sortStyle != null)
			? sortStyle.toSortJpa()
			: Sort.by(Sort.Order.desc("updatedAt"));

		Pageable pageable = PageRequest.of(page, size, sortJpa);

		TourismType categoryType = TourismType.fromOrThrow(category);
		log.info("{} 항목 리스트 조회 시작", categoryType.name());

		LocaleCode localeCode;
		try {
			localeCode = LocaleCode.from(locale);
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.badRequest().build();
		}

		if (q != null && !q.isBlank()) {
			log.info("통합 검색 요청 카테고리={} 로케일={} 테마ID={} q='{}' 페이지={} 크기={}",
				categoryType.name(), localeCode.getCode(), themeId, q, page, size);

			TourismType typeFilter = categoryFilter ? categoryType : null;
			int offset = page * size;

			Page<PlaceBriefSlimResponseDto> textPage = placeEsService.unifiedSearch(
				PageRequest.of(page, size, sortEs),
				localeCode, q, typeFilter, sortEs, userId
			);

			long textTotal = textPage.getTotalElements();
			List<PlaceBriefSlimResponseDto> textItems = textPage.getContent();

			if (offset + size <= textTotal) {
				return ResponseEntity.ok(PageResponse.fromPage(textPage));
			}

			List<PlaceBriefSlimResponseDto> items = new ArrayList<>(textItems);

			if (offset < textTotal) {
				int remainSize = size - items.size();

				Page<PlaceBriefSlimResponseDto> embPage = placeEsService.unifiedEmbeddingSearch(
					PageRequest.of(0, remainSize, sortEs),
					localeCode, q, typeFilter, sortEs, userId
				);

				long embTotal = embPage.getTotalElements();
				items.addAll(embPage.getContent());

				long total = textTotal + embTotal;
				return ResponseEntity.ok(PageResponse.fromMerged(page, size, total, items));
			}

			long embOffset = offset - textTotal;
			int embPageIdx = (int)Math.max(0, embOffset / size);

			Page<PlaceBriefSlimResponseDto> embPage = placeEsService.unifiedEmbeddingSearch(
				PageRequest.of(embPageIdx, size, sortEs),
				localeCode, q, typeFilter, sortEs, userId
			);

			long embTotal = embPage.getTotalElements();
			long total = textTotal + embTotal;

			return ResponseEntity.ok(PageResponse.fromMerged(page, size, total, embPage.getContent()));
		}
		PageResponse<PlaceBriefSlimResponseDto> body;
		if (themeId != null) {
			body = getService(categoryType).getPlaceListByTheme(pageable, themeId, userId, localeCode);
		} else {
			body = getService(categoryType).getPlaceList(pageable, localeCode, userId);
		}
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
		@AuthenticationPrincipal Integer userId,
		@Parameter(
			description = "카테고리",
			schema = @Schema(allowableValues = {"accommodation", "restaurant", "tour", "wrong-category"})
		)
		@PathVariable String category,

		@Parameter(description = "리소스 ID", example = "123")
		@PathVariable int id,

		@Parameter(description = "언어(로케일) 코드", example = "ko", schema = @Schema(allowableValues = {"ko", "en", "ja",
			"cn"}))
		@RequestParam(name = "locale", defaultValue = "ko") String locale
	) {
		TourismType categoryType = TourismType.fromOrThrow(category);
		LocaleCode localeCode;
		try {
			localeCode = LocaleCode.from(locale);
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.badRequest().build();
		}
		try {
			return ResponseEntity.of(getService(categoryType).getPlaceDetail(id, localeCode, userId)); // empty → 404
		} catch (TourApiException e) {
			return ResponseEntity.notFound().build();
		}
	}
	/*
	@GetMapping("/search/text")
	@Operation(
		summary = "텍스트 기반 검색",
		description = "multi_match 쿼리를 사용하여 places 인덱스에서 텍스트 검색을 수행합니다.",
		parameters = {
			@Parameter(name = "q", description = "검색어", example = "강남 카페")
		},
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "검색 성공",
				content = @Content(array = @ArraySchema(schema = @Schema(implementation = PlaceDocument.class)))
			)
		}
	)
	public ResponseEntity<List<PlaceBriefSlimResponseDto>> searchByText(@RequestParam String q) throws IOException {
		try {
			return ResponseEntity.ok(placeEsService.searchByText(q).stream()
				.map(PlaceBriefSlimResponseDto::from)
				.toList());
		} catch (Exception e) {
			log.error("텍스트 검색 실패: {}", q, e);
			return ResponseEntity.internalServerError().build();
		}
	}

	@GetMapping("/search/embedding")
	@Operation(
		summary = "임베딩 기반 검색 (fallback 포함)",
		description = "임베딩 검색을 우선 수행. 모델 오류 등으로 실패하면 multi_match 텍스트 검색으로 "
	)
	public ResponseEntity<List<PlaceBriefSlimResponseDto>> searchByEmbedding(@RequestParam String q) throws
		IOException {
		try {
			return ResponseEntity.ok(placeEsService.searchByEmbedding(q).stream()
				.map(PlaceBriefSlimResponseDto::from)
				.toList());
		} catch (IOException e) {
			log.error("의미 유사 검색 실패: {}", q, e);
			return ResponseEntity.internalServerError().build();
		}
	}

	 */

	@Operation(summary = "거리 기반 장소 검색", description = "위도, 경도, 거리, 언어를 기반으로 장소를 검색합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터", content = @Content),
		@ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
	})
	@GetMapping("/nearby")
	public ResponseEntity<List<PlaceBriefSlimResponseDto>> searchNearby(
		@AuthenticationPrincipal Integer userId,
		@Parameter(description = "위도", example = "37.5086534069")
		@RequestParam double lat,

		@Parameter(description = "경도", example = "129.095773005")
		@RequestParam double lng,

		@Parameter(description = "검색 거리 (예: 5km, 500m)", example = "500km")
		@RequestParam String distance,

		@Parameter(description = "결과 크기 (최대 개수)", example = "10",
			schema = @Schema(minimum = "1", maximum = "100"))
		@RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,

		@Parameter(description = "언어(로케일) 코드", example = "ko",
			schema = @Schema(allowableValues = {"ko", "en", "ja", "cn"}))
		@RequestParam(name = "locale", defaultValue = "ko") String locale
	) {
		log.info("거리 기반 장소 검색 lat={}, lng={}, distance={}, size={}, locale={}", lat, lng, distance, size, locale);

		LocaleCode lc = LocaleCode.from(locale);

		return ResponseEntity.ok(placeEsService.searchByDistance(lat, lng, distance, size, lc, userId));
	}

	@Operation(
		summary = "Elasticsearch 전체 리인덱스 실행",
		description = """
			'Execute' 버튼 클릭 시 전체 인덱스 삭제 후 재색인 수행합니다.
			"""
	)
	@GetMapping("/reindex")
	public void reindex(
		@Parameter(
			description = "보안 확인용 키워드. 'reindex'를 입력해야 실행됨.",
			example = "reindex"
		)
		@RequestParam String q
	) {
		if (!"reindex".equals(q))
			return;
		placeEsService.deleteAndReIndex();
		for (PlaceService service : placeService) {
			service.reindexFullEs();
		}
	}
}
