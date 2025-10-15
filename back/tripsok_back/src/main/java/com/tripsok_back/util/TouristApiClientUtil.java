package com.tripsok_back.util;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripsok_back.dto.tourApi.LclsCategoryItemResponseDto;
import com.tripsok_back.dto.tourApi.LclsSystmCodeRequestDto;
import com.tripsok_back.dto.tourApi.TourApiPlaceDetailRequestDto;
import com.tripsok_back.dto.tourApi.TourApiPlaceDetailResponseDto;
import com.tripsok_back.dto.tourApi.TourApiPlaceRequestDto;
import com.tripsok_back.dto.tourApi.TourApiPlaceResponseDto;
import com.tripsok_back.dto.tourApi.TourApiIntroRequestDto;
import com.tripsok_back.dto.tourApi.TourApiIntroResponseDto;
import com.tripsok_back.exception.InternalErrorCode;
import com.tripsok_back.exception.RetryableExternalException;
import com.tripsok_back.exception.ServiceBlockException;
import com.tripsok_back.exception.TourApiException;
import com.tripsok_back.type.LocaleCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Slf4j
@Component
@RequiredArgsConstructor
public class TouristApiClientUtil {

	private final WebClient touristApiWebClient;
	private final ObjectMapper objectMapper;
	private volatile boolean serviceBlocked = false;
	private volatile LocalDate blockedDate = LocalDate.now().minusDays(1);

	public List<TourApiPlaceResponseDto> fetchPlaceData(TourApiPlaceRequestDto dto) throws ServiceBlockException {
		isServiceBlocked();

		URI uri = UriComponentsBuilder
			.fromHttpUrl("https://apis.data.go.kr/B551011/KorService2/areaBasedList2")
			.queryParam("numOfRows", dto.getNumOfRows())
			.queryParam("pageNo", dto.getPageNo())
			.queryParam("MobileOS", dto.getMobileOS())
			.queryParam("MobileApp", dto.getMobileApp())
			.queryParam("_type", dto.getResponseType())
			.queryParam("arrange", dto.getArrange())
			.queryParam("areaCode", dto.getAreaCode())
			.queryParam("contentTypeId", dto.getContentTypeId())
			.queryParam("serviceKey", dto.getServiceKey())
			.build(true).toUri();

		log.info("관광 API 요청 URI: {}", uri);

		String body = fetchBodyWithRetry(uri);
		List<TourApiPlaceResponseDto> result = parseJsonList(body, "response.body.items.item",
			TourApiPlaceResponseDto.class);

		log.info("응답 처리 완료: {}개", result.size());
		return result;
	}

	public TourApiPlaceDetailResponseDto fetchPlaceDataDetail(TourApiPlaceDetailRequestDto dto) throws ServiceBlockException {
		isServiceBlocked();
		URI uri = UriComponentsBuilder
			.fromHttpUrl("https://apis.data.go.kr/B551011/KorService2/detailCommon2")
			.queryParam("MobileOS", dto.getMobileOS())
			.queryParam("MobileApp", dto.getMobileApp())
			.queryParam("_type", dto.getResponseType())
			.queryParam("contentId", dto.getContentId())
			.queryParam("serviceKey", dto.getServiceKey())
			.build(true).toUri();

		log.info("관광 API 상세정보 요청 URI: {}", uri);

		String body = fetchBodyWithRetry(uri);

		JsonNode itemNode;
		try {
			itemNode = resolvePath(objectMapper.readTree(body), "response.body.items.item");
		} catch (IOException e) {
			log.error("관광 API 상세정보 JSON 파싱 오류", e);
			throw new TourApiException(InternalErrorCode.JSON_PARSE_ERROR);
		}
		if (itemNode.isMissingNode() || itemNode.isNull()) {
			throw new TourApiException(InternalErrorCode.PLACE_DETAIL_NOT_FOUND);
		}

		if (itemNode.isArray()) {
			if (itemNode.isEmpty())
				throw new TourApiException(InternalErrorCode.PLACE_DETAIL_NOT_FOUND);
			return objectMapper.convertValue(itemNode.get(0), TourApiPlaceDetailResponseDto.class);
		}
		return objectMapper.convertValue(itemNode.get(0), TourApiPlaceDetailResponseDto.class);
	}

	public String fetchPlaceIntroRaw(TourApiIntroRequestDto dto) throws ServiceBlockException {
		isServiceBlocked();
		URI uri = UriComponentsBuilder
			.fromHttpUrl("https://apis.data.go.kr/B551011/KorService2/detailIntro2")
			.queryParam("numOfRows", dto.getNumOfRows())
			.queryParam("pageNo", dto.getPageNo())
			.queryParam("MobileOS", dto.getMobileOS())
			.queryParam("MobileApp", dto.getMobileApp())
			.queryParam("_type", dto.getResponseType())
			.queryParam("contentTypeId", dto.getContentTypeId())
			.queryParam("contentId", dto.getContentId())
			.queryParam("serviceKey", dto.getServiceKey())
			.build(true).toUri();

		String body = fetchBodyWithRetry(uri);
		try {
			JsonNode itemNode = resolvePath(objectMapper.readTree(body), "response.body.items.item");
			if (itemNode.isMissingNode() || itemNode.isNull())
				return null;
			JsonNode first = itemNode.isArray() ? (itemNode.isEmpty() ? null : itemNode.get(0)) : itemNode;
			return first != null ? objectMapper.writeValueAsString(first) : null;
		} catch (Exception e) {
			log.error("관광 API 소개정보 RAW 추출 실패", e);
			return null;
		}
	}

	public Map<String, LclsCategoryItemResponseDto> fetchCategories(LclsSystmCodeRequestDto dto,
		LocaleCode locale) throws ServiceBlockException {
		log.info("카테고리 {}언어 요청", locale.getCode());
		isServiceBlocked();

		String url = "https://apis.data.go.kr/B551011/{path}/lclsSystmCode2"
			.replace("{path}", locale.getUrlPath());

		URI uri = UriComponentsBuilder
			.fromHttpUrl(url)
			.queryParam("serviceKey", dto.getServiceKey())
			.queryParam("MobileApp", dto.getMobileApp())
			.queryParam("MobileOS", dto.getMobileOS())
			.queryParam("pageNo", dto.getPageNo())
			.queryParam("numOfRows", dto.getNumOfRows())
			.queryParam("_type", dto.getResponseType())
			.queryParam("lclsSystm1", dto.getLclsSystm1())
			.queryParam("lclsSystm2", dto.getLclsSystm2())
			.queryParam("lclsSystm3", dto.getLclsSystm3())
			.queryParam("lclsSystmListYn", dto.getLclsSystmListYn())
			.build(true).toUri();

		log.info("관광 분류코드 조회 URI: {}", uri);

		String body = fetchBodyWithRetry(uri);
		List<LclsCategoryItemResponseDto> list =
			parseJsonList(body, "response.body.items.item", LclsCategoryItemResponseDto.class);

		Map<String, LclsCategoryItemResponseDto> map = new LinkedHashMap<>();
		list.forEach(item -> map.put(item.getLclsSystm3Cd(), item));
		return map;
	}

	private <T> List<T> parseJsonList(String body, String pathExpr, Class<T> clazz) {
		try {
			JsonNode target = resolvePath(objectMapper.readTree(body), pathExpr);
			if (target.isMissingNode() || target.isNull())
				return Collections.emptyList();
			return objectMapper.readerForListOf(clazz).readValue(target);
		} catch (Exception e) {
			log.error("JSON 리스트 매핑 실패, path={}, class={}", pathExpr, clazz, e);
			throw new TourApiException(InternalErrorCode.JSON_PARSE_ERROR);
		}
	}

	private JsonNode resolvePath(JsonNode root, String pathExpr) {
		JsonNode cur = root;
		for (String key : pathExpr.split("\\.")) {
			cur = cur.path(key);
		}
		return cur;
	}

	public boolean isServiceBlocked() {
		if (serviceBlocked && blockedDate.equals(LocalDate.now())) {
			throw new ServiceBlockException();
		} else {
			serviceBlocked = false;
			blockedDate = null;
			return false;
		}

	}

	private void blockServiceForToday() {
		serviceBlocked = true;
		blockedDate = LocalDate.now();
		log.error("Tourist API 서비스가 오늘({}) 동안 차단되었습니다.", blockedDate);
	}

	private String fetchBodyWithRetry(URI uri) throws ServiceBlockException {
		isServiceBlocked();

		return touristApiWebClient.get()
			.uri(uri)
			.header("User-Agent", "Mozilla/5.0")
			.accept(MediaType.APPLICATION_JSON)
			.retrieve()
			.bodyToMono(String.class)
			.flatMap(this::inspectAndMaybeError)
			.retryWhen(
				Retry.backoff(3, Duration.ofMillis(500))
					.maxBackoff(Duration.ofSeconds(2))
					.filter(ex -> ex instanceof RetryableExternalException)
			)
			.onErrorResume(e -> {
				if (e instanceof TourApiException tae
					&& tae.getErrorCode() == InternalErrorCode.SERVICE_REQUEST_LIMIT_EXCEEDED) {
					log.error("Tourist API 요청 제한 초과 → 빈 응답 반환");
					return Mono.error(new ServiceBlockException());
				}
				if (e instanceof RetryableExternalException) {
					log.warn("Tourist API 재시도 실패, 일시적 오류 처리: {}", e.getMessage());
					return Mono.just("{}");
				}
				log.error("Tourist API 호출 중 알 수 없는 오류", e);
				return Mono.just("{}");
			})
			.block();
	}

	private Mono<String> inspectAndMaybeError(String body) {
		String trimmed = (body == null) ? "" : body.trim();

		try {
			JsonNode root = objectMapper.readTree(trimmed);
			String resultCode = resolvePath(root, "response.header.resultCode").asText(null);

			if ("22".equals(resultCode)) {
				log.warn("Tourist API 요청 제한 초과 → 오늘 차단");
				blockServiceForToday();
				return Mono.error(new TourApiException(InternalErrorCode.SERVICE_REQUEST_LIMIT_EXCEEDED));
			}
			if ("04".equals(resultCode)) {
				log.warn("에러 내용(resultCode=04) : {}", body);
				return Mono.error(new RetryableExternalException(InternalErrorCode.RETRYABLE_EXTERNAL_ERROR));
			}
			if ("01".equals(resultCode)) {
				log.warn("에러 내용(resultCode=01 APPLICATION 에러) : {}", body);
				return Mono.error(new RetryableExternalException(InternalErrorCode.RETRYABLE_EXTERNAL_ERROR));
			}
			return Mono.just(body);

		} catch (JsonProcessingException notJson) {
			String rc = extractResultCodeFromXml(trimmed);

			if ("22".equals(rc)) {
				log.warn("Tourist API XML 오류 (요청 제한 초과) → 오늘 차단");
				blockServiceForToday();
				return Mono.error(new TourApiException(InternalErrorCode.SERVICE_REQUEST_LIMIT_EXCEEDED));
			}
			if ("04".equals(rc)) {
				return Mono.error(new RetryableExternalException(InternalErrorCode.RETRYABLE_EXTERNAL_ERROR));
			}
			if ("01".equals(rc)) {
				log.warn("에러 내용(resultCode=01 APPLICATION 에러) : {}", body);
				return Mono.error(new RetryableExternalException(InternalErrorCode.RETRYABLE_EXTERNAL_ERROR));
			}
			log.warn("에러 내용(알 수 없는 코드) : {}", body);
			return Mono.error(new RetryableExternalException(InternalErrorCode.INTERNAL_SERVER_ERROR));
		}
	}

	private String extractResultCodeFromXml(String body) {
		if (body == null || body.isBlank())
			return null;
		if (body.contains("<resultCode>22</") || body.contains("<returnReasonCode>22</"))
			return "22";
		if (body.contains("<resultCode>04</") || body.contains("<returnReasonCode>04</"))
			return "04";
		if (body.contains("<resultCode>01</") || body.contains("<returnReasonCode>01</"))
			return "01";
		return null;
	}
}
