package com.tripsok_back.util;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.tripsok_back.dto.tourApi.LclsCategoryItemResponseDto;
import com.tripsok_back.dto.tourApi.LclsSystmCodeRequestDto;
import com.tripsok_back.dto.tourApi.TourApiPlaceDetailRequestDto;
import com.tripsok_back.dto.tourApi.TourApiPlaceDetailResponseDto;
import com.tripsok_back.dto.tourApi.TourApiPlaceRequestDto;
import com.tripsok_back.dto.tourApi.TourApiPlaceResponseDto;
import com.tripsok_back.exception.InternalErrorCode;
import com.tripsok_back.exception.TourApiException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TouristApiClientUtil {

	private final WebClient touristApiWebClient;
	private final ObjectMapper objectMapper;

	public List<TourApiPlaceResponseDto> fetchPlaceData(TourApiPlaceRequestDto dto) {

		ObjectWriter prettyPrinter = objectMapper.writerWithDefaultPrettyPrinter();

		URI uri = UriComponentsBuilder
			.fromHttpUrl("https://apis.data.go.kr/B551011/KorService2/areaBasedList2")
			.queryParam("numOfRows", dto.getNumOfRows())
			.queryParam("pageNo", dto.getPageNo())
			.queryParam("MobileOS", dto.getMobileOS())
			.queryParam("MobileApp", dto.getMobileApp())
			.queryParam("_type", dto.getType())
			.queryParam("arrange", dto.getArrange())
			.queryParam("areaCode", dto.getAreaCode())
			.queryParam("contentTypeId", dto.getContentTypeId())
			.queryParam("serviceKey", dto.getServiceKey())
			.build(true)
			.toUri();
		log.info("관광 API 요청 URI: {}", uri);

		String body = touristApiWebClient.get()
			.uri(uri)
			.header("User-Agent", "Mozilla/5.0")
			.retrieve()
			.bodyToMono(String.class)
			.block();

		try {
			log.info("관광 API 응답: \n{}", prettyPrinter.writeValueAsString(objectMapper.readValue(body, Object.class)));
			JsonNode root = objectMapper.readTree(body);
			log.info(root.asText());
			JsonNode itemsNode = root.path("response").path("body").path("items").path("item");

			if (itemsNode.isMissingNode() || itemsNode.isNull()) {
				log.warn("'item' 노드가 없음");
				return List.of();
			}

			List<TourApiPlaceResponseDto> result = objectMapper
				.readerForListOf(TourApiPlaceResponseDto.class)
				.readValue(itemsNode);

			log.info("응답 처리 완료: {}개", result.size());
			return result;

		} catch (Exception e) {
			log.error("JSON 파싱 오류", e);
			throw new RuntimeException("Tourist API 응답 파싱 오류", e);
		}
	}

	public TourApiPlaceDetailResponseDto fetchPlaceDataDetail(TourApiPlaceDetailRequestDto dto) throws
		TourApiException {

		ObjectWriter prettyPrinter = objectMapper.writerWithDefaultPrettyPrinter();

		URI uri = UriComponentsBuilder
			.fromHttpUrl("https://apis.data.go.kr/B551011/KorService2/detailCommon2")
			.queryParam("MobileOS", dto.getMobileOS())
			.queryParam("MobileApp", dto.getMobileApp())
			.queryParam("_type", dto.getResponseType())
			.queryParam("contentId", dto.getContentId())
			.queryParam("serviceKey", dto.getServiceKey())
			.build(true)  // true -> 인코딩된 상태로 생성
			.toUri();
		log.info("관광 API 상세정보 요청 URI: {}", uri);

		String body = touristApiWebClient.get()
			.uri(uri)
			.header("User-Agent", "Mozilla/5.0")
			.retrieve()
			.bodyToMono(String.class)
			.block();
		try {
			log.info("관광 API 응답: \n{}", prettyPrinter.writeValueAsString(objectMapper.readValue(body, Object.class)));

			JsonNode root = objectMapper.readTree(body);
			JsonNode itemsNode = root.path("response").path("body").path("items");

			JsonNode itemNode = itemsNode.path("item");
			if (itemNode.isMissingNode() || itemNode.isNull()) {
				log.warn("'item' 노드가 없음");
				throw new TourApiException(InternalErrorCode.PLACE_DETAIL_NOT_FOUND);
			}
			TourApiPlaceDetailResponseDto result;
			if (itemNode.isArray()) {
				if (itemNode.isEmpty()) {
					log.warn("상세 정보 API 응답 결과가 비어있습니다.");
					throw new TourApiException(InternalErrorCode.PLACE_DETAIL_NOT_FOUND);
				}
				result = objectMapper
					.readerFor(TourApiPlaceDetailResponseDto.class)
					.readValue(itemNode.get(0)); // 첫 원소만 사용
			} else if (itemNode.isObject()) {
				// 단일 오브젝트 케이스
				result = objectMapper
					.readerFor(TourApiPlaceDetailResponseDto.class)
					.readValue(itemNode);
			} else {
				log.warn("'item' 노드 타입이 예상과 다름: {}", itemNode.getNodeType());
				throw new TourApiException(InternalErrorCode.PLACE_DETAIL_NOT_FOUND);
			}

			log.info("응답 처리 완료: 1개");
			return result;
		}
		// ★ 도메인 예외는 그대로 통과
		catch (TourApiException e) {
			log.error("TourApi Exception occurred:", e);
			throw e;
		} catch (JsonProcessingException e) {
			log.error("JSON 파싱 오류", e);
			throw new TourApiException(InternalErrorCode.JSON_PARSE_ERROR);
		} catch (Exception e) {
			log.error("상세정보 조회 중 알 수 없는 오류", e);
			throw new TourApiException(InternalErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	public Map<String, LclsCategoryItemResponseDto> fetchCategories(LclsSystmCodeRequestDto dto) {
		URI uri = UriComponentsBuilder
			.fromHttpUrl("https://apis.data.go.kr/B551011/KorService2/lclsSystmCode2")
			.queryParam("serviceKey", dto.getServiceKey())
			.queryParam("MobileApp", orDefault(dto.getMobileApp(), "AppTest"))
			.queryParam("MobileOS", orDefault(dto.getMobileOS(), "ETC"))
			.queryParam("pageNo", orDefault(dto.getPageNo(), 1))
			.queryParam("numOfRows", orDefault(dto.getNumOfRows(), 300))
			.queryParam("_type", orDefault(dto.get_type(), "json"))
			.queryParam("lclsSystm1", nullToEmpty(dto.getLclsSystm1()))
			.queryParam("lclsSystm2", nullToEmpty(dto.getLclsSystm2()))
			.queryParam("lclsSystm3", nullToEmpty(dto.getLclsSystm3()))
			.queryParam("lclsSystmListYn", orDefault(dto.getLclsSystmListYn(), "Y"))
			.build(true)
			.toUri();

		log.info("관광 분류코드 조회 URI: {}", uri);

		String body = touristApiWebClient.get()
			.uri(uri)
			.header("User-Agent", "Mozilla/5.0")
			.retrieve()
			.bodyToMono(String.class)
			.block();

		try {
			JsonNode root = objectMapper.readTree(body);
			JsonNode itemsNode = root.path("response").path("body").path("items").path("item");
			if (itemsNode.isMissingNode() || itemsNode.isNull()) {
				log.warn("분류코드 'item' 노드 없음");
				return Collections.emptyMap();
			}
			Map<String, LclsCategoryItemResponseDto> map = new LinkedHashMap<>();

			List<LclsCategoryItemResponseDto> list =
				objectMapper.readerForListOf(LclsCategoryItemResponseDto.class)
					.readValue(itemsNode);
			for (LclsCategoryItemResponseDto dtoItem : list) {
				map.put(dtoItem.getLclsSystm3Cd(), dtoItem);
			}
			return map;
		} catch (Exception e) {
			log.error("분류코드 파싱 실패", e);
			throw new RuntimeException("KorService2/lclsSystmCode2 응답 파싱 오류", e);
		}
	}

	private static <T> T orDefault(T v, T d) {
		return v == null ? d : v;
	}

	private static String nullToEmpty(String s) {
		return s == null ? "" : s;
	}

}