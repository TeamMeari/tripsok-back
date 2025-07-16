package com.tripsok_back.batch;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripsok_back.dto.TouristItemRequestDto;
import com.tripsok_back.dto.TouristItemResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TouristApiClient {

	@Qualifier("touristApiWebClient")
	private final WebClient touristApiWebClient;
	private final ObjectMapper objectMapper;

	public List<TouristItemResponseDto> fetchTouristData(TouristItemRequestDto dto) {
		String uri = UriComponentsBuilder.fromPath("/areaBasedList2")
			.queryParam("numOfRows", dto.getNumOfRows())
			.queryParam("pageNo", dto.getPageNo())
			.queryParam("MobileOS", dto.getMobileOS())
			.queryParam("MobileApp", dto.getMobileApp())
			.queryParam("_type", dto.getType())
			.queryParam("arrange", dto.getArrange())
			.queryParam("areaCode", dto.getAreaCode())
			.queryParam("serviceKey", dto.getServiceKey())
			.build()
			.toUriString();

		log.info("▶ 관광 API 요청 URI: {}", uri);

		String json = touristApiWebClient.get()
			.uri(uri)
			.retrieve()
			.bodyToMono(String.class)
			.block();

		try {
			JsonNode root = objectMapper.readTree(json);
			JsonNode itemsNode = root.path("response").path("body").path("items").path("item");

			// item이 배열이 아닐 수도 있으므로 방어코드 포함
			if (itemsNode.isMissingNode() || itemsNode.isNull()) {
				log.warn("'item' 노드가 없음");
				return List.of();
			}

			List<TouristItemResponseDto> result = objectMapper
				.readerForListOf(TouristItemResponseDto.class)
				.readValue(itemsNode);

			log.info("응답 처리 완료: {}개", result.size());
			return result;

		} catch (Exception e) {
			log.error("JSON 파싱 오류", e);
			throw new RuntimeException("Tourist API 응답 파싱 오류", e);
		}
	}
}