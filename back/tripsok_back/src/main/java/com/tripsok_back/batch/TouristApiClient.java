package com.tripsok_back.batch;

import java.net.URI;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.tripsok_back.dto.TouristItemRequestDto;
import com.tripsok_back.dto.TouristItemResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TouristApiClient {

	private final WebClient touristApiWebClient;
	private final ObjectMapper objectMapper;

	public List<TouristItemResponseDto> fetchTouristData(TouristItemRequestDto dto) throws JsonProcessingException {

		ObjectWriter prettyPrinter = objectMapper.writerWithDefaultPrettyPrinter();

		URI uri = UriComponentsBuilder
			.fromHttpUrl("https://apis.data.go.kr/B551011/KorService2/areaBasedList2")
			.queryParam("numOfRows", 100)
			.queryParam("pageNo", 1)
			.queryParam("MobileOS", "ETC")
			.queryParam("MobileApp", "tripsok-batch")
			.queryParam("_type", "json")
			.queryParam("arrange", "R")
			.queryParam("areaCode", 32)
			.queryParam("serviceKey",
				dto.getServiceKey()) // 인코딩 안 함
			.build(true) // 이걸로!
			.toUri();
		log.info("관광 API 요청 URI: {}", uri);

		String body = touristApiWebClient.get()
			.uri(uri)
			.header("User-Agent", "Mozilla/5.0") // ← 추가
			.retrieve()
			.bodyToMono(String.class)
			.block();
		log.info("관광 API 응답: \n{}", prettyPrinter.writeValueAsString(objectMapper.readValue(body, Object.class)));
		try {
			JsonNode root = objectMapper.readTree(body);
			log.info(root.asText());
			JsonNode itemsNode = root.path("response").path("body").path("items").path("item");

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