package com.tripsok_back.service.search;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.tripsok_back.dto.place.PlaceBriefSlimResponseDto;
import com.tripsok_back.dto.place.PlaceDocument;
import com.tripsok_back.model.place.Place;
import com.tripsok_back.model.user.InterestPlace;
import com.tripsok_back.repository.place.PlaceRepository;
import com.tripsok_back.repository.user.InterestPlaceRepository;
import com.tripsok_back.service.theme.ThemeService;
import com.tripsok_back.type.LocaleCode;
import com.tripsok_back.type.TourismType;
import com.tripsok_back.util.EmbeddingUtil;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.DistanceUnit;
import co.elastic.clients.elasticsearch._types.GeoLocation;
import co.elastic.clients.elasticsearch._types.LatLonGeoLocation;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlaceEsService {

	private static final String INDEX = "places";
	private static final String MAPPING_PATH = "elasticsearch/mappings/places-mapping.json";
	private final ElasticsearchClient esClient;
	private final EmbeddingUtil embeddingUtil;
	private final PlaceRepository placeRepository;
	private final InterestPlaceRepository interestPlaceRepository;
	private final ThemeService themeService;

	public String indexPlaceDocument(PlaceDocument doc) throws Exception {
		IndexResponse res = esClient.index(i -> i
			.index("places")
			.id(doc.getId())
			.document(doc)
		);
		return res.result().jsonValue();
	}

	public int indexPlaceDocuments(Place place) {
		if (place == null)
			return 0;
		int count = 0;
		TourismType type = TourismType.fromOrThrow(place);
		for (LocaleCode lc : List.of(LocaleCode.KO, LocaleCode.EN, LocaleCode.JA, LocaleCode.CN)) {
			try {
				if (place.getPlaceTr(lc) == null)
					continue;
				PlaceDocument doc = PlaceDocument.fromEntity(place, lc, type, embeddingUtil);
				indexPlaceDocument(doc);
				count++;
			} catch (Exception e) {
				log.warn("전체 문서 색인 실패 place={} locale={} 이유={}", place.getId(), lc.getCode(),
					e.getMessage());
			}
		}
		return count;
	}

	public Page<PlaceBriefSlimResponseDto> unifiedSearch(
		Pageable pageable, LocaleCode lc, String q, TourismType type, Sort sortArg, Integer themeId, Integer userId) {

		long start = System.currentTimeMillis();
		try {
			log.info("ES 통합 검색 시작 q='{}' 로케일={} 타입={} 페이지={} 크기={} 테마ID={}",
				q, lc != null ? lc.getCode() : null, type != null ? type.name() : null,
				pageable.getPageNumber(), pageable.getPageSize(), themeId);

			final boolean hasQuery = q != null && !q.isBlank();
			String[] fields = fieldsForLocale(lc);

			Query text = hasQuery
				? Query.of(b -> b.multiMatch(mm -> mm
				.query(q)
				.fields(Arrays.asList(fields))
				.type(TextQueryType.BestFields)
				.tieBreaker(0.3)
			))
				: Query.of(b -> b.matchAll(m -> m));

			SearchRequest req = SearchRequest.of(s -> {
				SearchRequest.Builder b = s
					.index(INDEX)
					.from((int)pageable.getOffset())
					.size(pageable.getPageSize())
					.query(qb -> qb.bool(bl -> {
						bl.must(text);
						if (lc != null) {
							bl.filter(f -> f.term(t -> t.field("locale").value(lc.getCode())));
						}
						if (type != null) {
							bl.filter(f -> f.term(t -> t.field("type").value(type.name())));
						}
						if (themeId != null) {
							String themeType = themeService.getThemeType(themeId);
							if (themeType != null) {
								bl.filter(f -> f.term(t -> t.field("themes")
									.value(themeType)));
							}
						}
						return bl;
					}))
					.source(src -> src.filter(flt -> flt.includes(
						"placeId", "locale", "title", "summary", "type", "lat", "lng",
						"thumbnailUrl", "like", "view", "updatedAt", "themes"
					)));

				if (sortArg != null && sortArg.isSorted()) {
					for (Sort.Order order : sortArg) {
						String field = order.getProperty();
						SortOrder esOrder = order.isAscending() ? SortOrder.Asc : SortOrder.Desc;
						if ("_score".equals(field)) {
							b.sort(ss -> ss.score(sc -> sc.order(esOrder)));
						} else {
							b.sort(ss -> ss.field(f -> f.field(field).order(esOrder)));
						}
					}
				} else {
					b.sort(ss -> ss.score(sc -> sc.order(SortOrder.Desc)));
					b.sort(ss -> ss.field(f -> f.field("id").order(SortOrder.Asc)));
				}

				return b;
			});

			SearchResponse<PlaceDocument> res = esClient.search(req, PlaceDocument.class);
			Set<Integer> placeIds = res.hits().hits().stream()
				.map(Hit::source)
				.filter(Objects::nonNull)
				.map(PlaceDocument::getPlaceId)
				.filter(Objects::nonNull)
				.map(Integer::parseInt)
				.collect(Collectors.toSet());
			List<InterestPlace> interestPlaces = interestPlaceRepository.findByUser_IdAndPlace_IdIn(userId, placeIds);
			Set<Integer> likedPlaceIds = interestPlaces.stream()
				.map(ip -> ip.getPlace().getId())
				.collect(Collectors.toSet());
			log.info(likedPlaceIds.toString());
			List<PlaceBriefSlimResponseDto> items = new ArrayList<>(res.hits().hits().size());
			for (Hit<PlaceDocument> h : res.hits().hits()) {
				PlaceDocument d = h.source();
				if (d != null)
					items.add(
						PlaceBriefSlimResponseDto.from(d, likedPlaceIds.contains(Integer.parseInt(d.getPlaceId()))));
			}
			long total = res.hits().total() != null ? res.hits().total().value() : items.size();

			log.info("ES 통합 검색 완료 조회수={} 전체={} 소요={}ms",
				items.size(), total, (System.currentTimeMillis() - start));

			return new PageImpl<>(items, pageable, total);

		} catch (Exception e) {
			log.warn("ES 통합 검색 실패: {}", e.getMessage());
			return Page.empty(pageable);
		}
	}

	public Page<PlaceBriefSlimResponseDto> unifiedEmbeddingSearch(
		Pageable pageable, LocaleCode lc, String q, TourismType type, Sort sortArg, Integer themeId, Integer userId) {

		long start = System.currentTimeMillis();
		try {
			log.info("ES 임베딩 통합 검색 시작 q='{}' 로케일={} 타입={} 페이지={} 크기={} 테마ID={}",
				q, lc != null ? lc.getCode() : null, type != null ? type.name() : null,
				pageable.getPageNumber(), pageable.getPageSize(), themeId);

			List<Float> vector = embeddingUtil.embed(q);

			int from = (int)pageable.getOffset();
			int size = pageable.getPageSize();
			int k = Math.min(from + size, 10_000);
			int nc = Math.min(Math.max(k * 3, 200), 10_000);

			SearchRequest req = SearchRequest.of(s -> {
				SearchRequest.Builder b = s
					.index(INDEX)
					.from(from)
					.size(size)
					.minScore(0.7)
					.knn(knn -> knn
						.field("embedding")
						.queryVector(vector)
						.k(k)
						.numCandidates(nc)
						.filter(f -> f.bool(bl -> {
							if (lc != null) {
								bl.filter(qb -> qb.term(t -> t.field("locale").value(lc.getCode())));
							}
							if (type != null) {
								bl.filter(qb -> qb.term(t -> t.field("type").value(type.name())));
							}
							if (themeId != null) {
								String themeType = themeService.getThemeType(themeId);
								if (themeType != null) {
									bl.filter(qb -> qb.term(t -> t
										.field("themes")
										.value(themeType)));
								}
							}
							return bl;
						}))
					)
					.source(src -> src.filter(flt -> flt.includes(
						"placeId", "locale", "title", "summary", "type", "lat", "lng",
						"thumbnailUrl", "like", "view", "updatedAt"
					)))
					.trackTotalHits(t -> t.enabled(false));

				if (sortArg != null && sortArg.isSorted()) {
					for (Sort.Order order : sortArg) {
						String field = order.getProperty();
						SortOrder esOrder = order.isAscending() ? SortOrder.Asc : SortOrder.Desc;

						if ("_score".equals(field)) {
							b.sort(ss -> ss.score(sc -> sc.order(esOrder)));
						} else {
							b.sort(ss -> ss.field(f -> f.field(field).order(esOrder)));
						}
					}
				} else {
					b.sort(ss -> ss.score(sc -> sc.order(SortOrder.Desc)));
					b.sort(ss -> ss.field(f -> f.field("id").order(SortOrder.Asc)));
				}

				return b;
			});

			SearchResponse<PlaceDocument> res = esClient.search(req, PlaceDocument.class);
			Set<Integer> placeIds = res.hits().hits().stream()
				.map(Hit::source)
				.filter(Objects::nonNull)
				.map(PlaceDocument::getPlaceId)
				.filter(Objects::nonNull)
				.map(Integer::parseInt)
				.collect(Collectors.toSet());
			List<InterestPlace> interestPlaces = interestPlaceRepository.findByUser_IdAndPlace_IdIn(userId, placeIds);
			Set<Integer> likedPlaceIds = interestPlaces.stream()
				.map(ip -> ip.getPlace().getId())
				.collect(Collectors.toSet());
			List<PlaceBriefSlimResponseDto> items = new ArrayList<>();
			for (Hit<PlaceDocument> h : res.hits().hits()) {
				if (h.score() != null && h.score() >= 0.7) {
					PlaceDocument d = h.source();
					if (d != null)
						items.add(
							PlaceBriefSlimResponseDto.from(d,
								likedPlaceIds.contains(Integer.parseInt(d.getPlaceId()))));
				}
			}

			long total = items.size();
			log.info("ES 임베딩 통합 검색 완료 조회수={} 전체(근사)={} 소요={}ms",
				items.size(), total, (System.currentTimeMillis() - start));

			return new PageImpl<>(items, pageable, total);

		} catch (Exception e) {
			log.warn("ES 임베딩 통합 검색 실패: {}", e.getMessage());
			return Page.empty(pageable);
		}
	}

	/*
	public List<PlaceDocument> searchByText(String query) throws IOException {
		long startMs = System.currentTimeMillis();
		log.info("ES 장소 텍스트 검색 시작: q='{}'", query);
		SearchResponse<PlaceDocument> searchResponse = esClient.search(s -> s
				.index("places")
				.query(q -> q
					.multiMatch(m -> m
						.query(query)
						.fields("title", "summary", "address", "information")
					)
				),
			PlaceDocument.class
		);
		List<PlaceDocument> results = searchResponse.hits().hits().stream()
			.map(Hit::source)
			.toList();
		long totalHits = searchResponse.hits().total() != null ? searchResponse.hits().total().value() : results.size();
		log.info("ES 장소 텍스트 검색 완료: 검색결과={} 전체건수={} 소요시간={}ms", results.size(), totalHits,
			System.currentTimeMillis() - startMs);

		return results;
	}
	*/
	/*
	public List<PlaceDocument> searchByEmbedding(String query) throws IOException {
		try {
			List<Float> queryVector = embeddingUtil.embed(query);
			long startMs = System.currentTimeMillis();
			log.info("ES 장소 임베딩 검색 시작: q='{}'", query);
			SearchResponse<PlaceDocument> searchResponse = esClient.search(s -> s
					.index("places")
					.knn(knn -> knn
						.field("embedding")
						.queryVector(queryVector)
						.k(5)
						.numCandidates(100)
					),
				PlaceDocument.class
			);
			List<PlaceDocument> results = searchResponse.hits().hits().stream()
				.map(Hit::source)
				.toList();
			log.info("ES 장소 임베딩 검색 완료: 검색결과={}개 소요시간={}ms",
				results.size(),
				(System.currentTimeMillis() - startMs));
			return results;

		} catch (Exception e) {
			log.warn("임베딩 검색 실패 → 텍스트 검색으로 대체 실행. query={}", query, e);
			return searchByText(query);
		}
	}
*/
	private String[] fieldsForLocale(LocaleCode locale) {
		String nameAddressSuffix = locale.esNameAddrSuffix();
		String summaryInfoSuffix = locale.esSummaryInfoSuffix();
		if (nameAddressSuffix == null || summaryInfoSuffix == null) {
			return new String[] {"title^4", "address^2", "summary^2", "information"};
		}
		return new String[] {
			"title." + nameAddressSuffix + "^5",
			"address." + nameAddressSuffix + "^3",
			"summary." + summaryInfoSuffix + "^3",
			"information." + summaryInfoSuffix,
			"title^4",
			"address^2",
			"summary^2",
			"information"
		};
	}

	public boolean checkIfReindexNeeds() {
		try {
			SearchResponse<Map> res = esClient.search(s -> s
					.index("places")
					.size(0)
					.aggregations("distinct_places", a -> a
						.cardinality(c -> c.field("placeId.keyword"))
					),
				Map.class
			);

			long distinctPlaces = res.aggregations()
				.get("distinct_places")
				.cardinality()
				.value();

			Integer accommodationCount = placeRepository.countByAccommodationIsNotNull();
			Integer tourCount = placeRepository.countByTourIsNotNull();
			Integer restaurantCount = placeRepository.countByRestaurantIsNotNull();
			Integer sum = accommodationCount + tourCount + restaurantCount;
			if (distinctPlaces == sum) {
				log.info("ES색인 도큐먼트 수 {}, TRs 장소 수 {}[숙소 :{}, 여행 :{}, 식당 :{}]", distinctPlaces, sum,
					accommodationCount, tourCount, restaurantCount);
				return true;
			} else {
				log.info("ES색인 도큐먼트 수 {}, TRs 장소 수 {}[숙소 :{}, 여행 :{}, 식당 :{}]", distinctPlaces, sum,
					accommodationCount, tourCount, restaurantCount);
				return false;
			}
		} catch (Exception e) {
			throw new RuntimeException("ES count 실패", e);
		}
	}

	public void createIndexIfMissing() {
		try {
			boolean exists = esClient.indices()
				.exists(ExistsRequest.of(b -> b.index(INDEX)))
				.value();
			if (exists)
				return;

			ClassPathResource mapping = new ClassPathResource(MAPPING_PATH);
			if (!mapping.exists()) {
				throw new IllegalStateException(
					"매핑 파일이 없습니다: classpath:" + MAPPING_PATH
				);
			}

			try (InputStream json = mapping.getInputStream()) {
				CreateIndexResponse cir = esClient.indices().create(c -> c.index(INDEX).withJson(json));
				log.info("ES index '{}' 생성됨 {} (ack={})", INDEX, MAPPING_PATH, cir.acknowledged());
			}
		} catch (Exception e) {
			throw new RuntimeException("ES 인덱스 생성 실패", e);
		}
	}

	public List<PlaceBriefSlimResponseDto> searchByDistance(double lat, double lng, String distance, int size,
		LocaleCode locale, Integer userId) {
		try {

			SearchRequest req = SearchRequest.of(s -> s
				.index(INDEX)
				.size(size)
				.query(q -> q
					.bool(b -> b
						.must(m -> m
							.geoDistance(g -> g
								.field("location")
								.distance(distance)
								.location(GeoLocation.of(l -> l.latlon(
									LatLonGeoLocation.of(ll -> ll.lat(lat).lon(lng))
								)))
							)
						)
						.filter(f -> f
							.term(t -> t.field("locale").value(locale.name().toLowerCase()))
						)
					)
				)
				.sort(so -> so.geoDistance(g -> g
					.field("location")
					.location(GeoLocation.of(l -> l.latlon(
						LatLonGeoLocation.of(ll -> ll.lat(lat).lon(lng))
					)))
					.unit(DistanceUnit.Kilometers)
					.order(SortOrder.Asc)
				))

				.source(src -> src.filter(flt -> flt.includes(
					"placeId", "locale", "title", "type", "lat", "lng", "summary",
					"thumbnailUrl", "like", "view", "updatedAt"
				)))
			);

			SearchResponse<PlaceDocument> res = esClient.search(req, PlaceDocument.class);
			log.info("거리 기반 검색 완료 lat={}, lng={}, distance={}, 결과={}", lat, lng, distance, res.hits().hits().size());

			List<PlaceBriefSlimResponseDto> items = new ArrayList<>();
			Set<Integer> placeIds = res.hits().hits().stream()
				.map(Hit::source)
				.filter(Objects::nonNull)
				.map(PlaceDocument::getPlaceId)
				.filter(Objects::nonNull)
				.map(Integer::parseInt)
				.collect(Collectors.toSet());
			List<InterestPlace> interestPlaces = interestPlaceRepository.findByUser_IdAndPlace_IdIn(userId, placeIds);
			Set<Integer> likedPlaceIds = interestPlaces.stream()
				.map(ip -> ip.getPlace().getId())
				.collect(Collectors.toSet());
			for (Hit<PlaceDocument> h : res.hits().hits()) {
				PlaceDocument d = h.source();
				if (d != null)
					items.add(
						PlaceBriefSlimResponseDto.from(d, likedPlaceIds.contains(Integer.parseInt(d.getPlaceId()))));
			}
			return items;
		} catch (IOException e) {
			log.error("거리 기반 검색 실패", e);
			throw new RuntimeException("ES 거리 기반 검색 실패", e);
		}
	}

	public void deleteAndReIndex() {
		try {
			DeleteIndexResponse response = esClient.indices().delete(d -> d.index(INDEX));
			log.info("삭제 응답: {}", response.acknowledged());
		} catch (Exception e) {
			log.info("삭제가 정상적으로 이루어지지 않음: {}", e.getMessage());
		}
	}
}
