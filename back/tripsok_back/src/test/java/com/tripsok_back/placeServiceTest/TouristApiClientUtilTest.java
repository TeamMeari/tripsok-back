package com.tripsok_back.placeServiceTest;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripsok_back.dto.tourApi.LclsCategoryItemResponseDto;
import com.tripsok_back.dto.tourApi.LclsSystmCodeRequestDto;
import com.tripsok_back.dto.tourApi.TourApiPlaceDetailRequestDto;
import com.tripsok_back.dto.tourApi.TourApiPlaceRequestDto;
import com.tripsok_back.exception.InternalErrorCode;
import com.tripsok_back.exception.TourApiException;
import com.tripsok_back.type.TourismType;
import com.tripsok_back.util.TouristApiClientUtil;

@ExtendWith(MockitoExtension.class) // Mockito만 확장(스프링 컨텍스트 없음)
class TouristApiClientUtilTest {

	// ---------- 테스트 고정값 ----------
	private static final int NUM_OF_ROWS = 3;
	private static final int PAGE_NO = 1;
	private static final String DUMMY_SERVICE_KEY = "dummy";
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	// ---------- SUT 생성 ----------
	private TouristApiClientUtil newSut(WebClient client) {
		return new TouristApiClientUtil(client, OBJECT_MAPPER);
	}

	// ---------- WebClient 스텁 ----------

	/** URI의 포함 문자열로 응답 바디를 라우팅하는 테스트용 WebClient */
	private WebClient stubbedWebClientByUrl(Map<String, String> urlContainsToBody, HttpStatus status) {
		ExchangeFunction exchange = mock(ExchangeFunction.class);
		when(exchange.exchange(any(ClientRequest.class))).thenAnswer(inv -> {
			ClientRequest req = inv.getArgument(0);
			String url = req.url().toString();
			for (var e : urlContainsToBody.entrySet()) {
				if (url.contains(e.getKey())) {
					ClientResponse resp = ClientResponse.create(status)
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
						.body(e.getValue())
						.build();
					return reactor.core.publisher.Mono.just(resp);
				}
			}
			return reactor.core.publisher.Mono.just(ClientResponse.create(HttpStatus.NOT_FOUND).build());
		});
		return WebClient.builder().exchangeFunction(exchange).build();
	}

	// ---------- JSON 픽스처 ----------
	private static final String PLACE_LIST_OK = """
		  {
		    "response": {
		      "header": {
		        "resultCode": "0000",
		        "resultMsg": "OK"
		      },
		      "body": {
		        "items": {
		          "item": [
		            {
		              "addr1": "강원특별자치도 고성군 토성면 봉포2길 12",
		              "addr2": "",
		              "areacode": "32",
		              "cat1": "B02",
		              "cat2": "B0201",
		              "cat3": "B02010700",
		              "contentid": "3513119",
		              "contenttypeid": "32",
		              "createdtime": "20250725142243",
		              "firstimage": "http://tong.visitkorea.or.kr/cms/resource/92/3513092_image2_1.jpg",
		              "firstimage2": "http://tong.visitkorea.or.kr/cms/resource/92/3513092_image3_1.jpg",
		              "cpyrhtDivCd": "Type3",
		              "mapx": "128.5658200160",
		              "mapy": "38.2514070152",
		              "mlevel": "6",
		              "modifiedtime": "20250725142525",
		              "sigungucode": "2",
		              "tel": "",
		              "title": "삼박한집",
		              "zipcode": "24765",
		              "lDongRegnCd": "51",
		              "lDongSignguCd": "820",
		              "lclsSystm1": "AC",
		              "lclsSystm2": "AC03",
		              "lclsSystm3": "AC030100"
		            },
		            {
		              "addr1": "강원특별자치도 횡성군 둔내면 두원3길 26",
		              "addr2": "",
		              "areacode": "32",
		              "cat1": "B02",
		              "cat2": "B0201",
		              "cat3": "B02010700",
		              "contentid": "3490410",
		              "contenttypeid": "32",
		              "createdtime": "20250512144037",
		              "firstimage": "http://tong.visitkorea.or.kr/cms/resource/73/3490273_image2_1.jpg",
		              "firstimage2": "http://tong.visitkorea.or.kr/cms/resource/73/3490273_image3_1.jpg",
		              "cpyrhtDivCd": "Type3",
		              "mapx": "128.2556044466",
		              "mapy": "37.4975552091",
		              "mlevel": "6",
		              "modifiedtime": "20250512145901",
		              "sigungucode": "18",
		              "tel": "",
		              "title": "숲속의연인별",
		              "zipcode": "25263",
		              "lDongRegnCd": "51",
		              "lDongSignguCd": "730",
		              "lclsSystm1": "AC",
		              "lclsSystm2": "AC03",
		              "lclsSystm3": "AC030100"
		            },
		            {
		              "addr1": "강원특별자치도 양양군 현남면 개매길 260",
		              "addr2": "",
		              "areacode": "32",
		              "cat1": "B02",
		              "cat2": "B0201",
		              "cat3": "B02010900",
		              "contentid": "3469367",
		              "contenttypeid": "32",
		              "createdtime": "20250122152920",
		              "firstimage": "http://tong.visitkorea.or.kr/cms/resource/56/3469356_image2_1.jpg",
		              "firstimage2": "http://tong.visitkorea.or.kr/cms/resource/56/3469356_image3_1.jpg",
		              "cpyrhtDivCd": "Type3",
		              "mapx": "128.7703943992",
		              "mapy": "37.9435939963",
		              "mlevel": "6",
		              "modifiedtime": "20250205094817",
		              "sigungucode": "7",
		              "tel": "",
		              "title": "더앤리조트",
		              "zipcode": "25056",
		              "lDongRegnCd": "51",
		              "lDongSignguCd": "830",
		              "lclsSystm1": "AC",
		              "lclsSystm2": "AC04",
		              "lclsSystm3": "AC040100"
		            }
		          ]
		        },
		        "numOfRows": 3,
		        "pageNo": 1,
		        "totalCount": 616
		      }
		    }
		  }
		""";

	private static final String PLACE_LIST_EMPTY = """
		  { "response": { "body": { "items": { "item": [] } } } }
		""";

	private static final String PLACE_LIST_NO_ITEM = """
		  { "response": { "body": { "items": { } } } }
		""";

	private static final String PLACE_DETAIL_OK = """
		{
		  "response": {
		    "header": {
		      "resultCode": "0000",
		      "resultMsg": "OK"
		    },
		    "body": {
		      "items": {
		        "item": [
		          {
		            "contentid": "3513119",
		            "contenttypeid": "32",
		            "title": "삼박한집",
		            "createdtime": "20250725142243",
		            "modifiedtime": "20250725142525",
		            "tel": "",
		            "telname": "",
		            "homepage": "<a href=\\"https://www.sambakhanzip.com/\\" target=\\"_blank\\" title=\\"새창 : 삼박한집 홈페이지로 이동\\">https://www.sambakhanzip.com/</a>",
		            "firstimage": "http://tong.visitkorea.or.kr/cms/resource/92/3513092_image2_1.jpg",
		            "firstimage2": "http://tong.visitkorea.or.kr/cms/resource/92/3513092_image3_1.jpg",
		            "cpyrhtDivCd": "Type3",
		            "areacode": "32",
		            "sigungucode": "2",
		            "lDongRegnCd": "51",
		            "lDongSignguCd": "820",
		            "lclsSystm1": "AC",
		            "lclsSystm2": "AC03",
		            "lclsSystm3": "AC030100",
		            "cat1": "B02",
		            "cat2": "B0201",
		            "cat3": "B02010700",
		            "addr1": "강원특별자치도 고성군 토성면 봉포2길 12",
		            "addr2": "",
		            "zipcode": "24765",
		            "mapx": "128.5658200160",
		            "mapy": "38.2514070152",
		            "mlevel": "6",
		            "overview": "삼박한 집은 숙박업 20년 경력의 집사가 새로 지은 강원도 고성 바닷가 감성 숙소이다. 80년생 일본 정통 히노끼탕이 전 객실 준비되어있다. 3면 침대 가드 및 18종의 아기용품이 있다. 강원건축문화상 특별상을 수상하였으며, 서울시 공공 건축가 설계이며 문화관광부 세이프 스테이에도 선정되었다."
		          }
		        ]
		      },
		      "numOfRows": 1,
		      "pageNo": 1,
		      "totalCount": 1
		    }
		  }
		}
		""";

	private static final String PLACE_DETAIL_NO_ITEM = """
		  { "response": { "body": { "items": { } } } }
		""";

	private static final String INVALID_JSON = "{><}{ invalid json ... ";

	private static final String CAT_OK = """
		  {
		    "response": {
		      "header": {
		        "resultCode": "0000",
		        "resultMsg": "OK"
		      },
		      "body": {
		        "items": {
		          "item": [
		            {
		              "lclsSystm1Cd": "AC",
		              "lclsSystm1Nm": "숙박",
		              "lclsSystm2Cd": "AC01",
		              "lclsSystm2Nm": "호텔",
		              "lclsSystm3Cd": "AC010100",
		              "lclsSystm3Nm": "호텔",
		              "rnum": 1
		            },
		            {
		              "lclsSystm1Cd": "AC",
		              "lclsSystm1Nm": "숙박",
		              "lclsSystm2Cd": "AC02",
		              "lclsSystm2Nm": "콘도미니엄",
		              "lclsSystm3Cd": "AC020100",
		              "lclsSystm3Nm": "콘도",
		              "rnum": 2
		            },
		            {
		              "lclsSystm1Cd": "AC",
		              "lclsSystm1Nm": "숙박",
		              "lclsSystm2Cd": "AC02",
		              "lclsSystm2Nm": "콘도미니엄",
		              "lclsSystm3Cd": "AC020200",
		              "lclsSystm3Nm": "레지던스",
		              "rnum": 3
		            }
		          ]
		        },
		        "numOfRows": 3,
		        "pageNo": 1,
		        "totalCount": 245
		      }
		    }
		  }
		""";

	private static final String CAT_NO_ITEM = """
		  { "response": { "body": { "items": { } } } }
		""";

	@Nested
	@DisplayName("fetchPlaceData")
	class FetchPlaceData {

		@Test
		@DisplayName("item이 있으면 리스트 파싱 성공")
		void itemExists_returnsList() {
			// Given
			WebClient client = stubbedWebClientByUrl(Map.of("areaBasedList2", PLACE_LIST_OK), HttpStatus.OK);
			var sut = newSut(client);
			var req = TourApiPlaceRequestDto.builder()
				.numOfRows(NUM_OF_ROWS)
				.pageNo(PAGE_NO)
				.mobileOS("ETC")
				.mobileApp("tripsok-batch")
				.type("json")
				.arrange("R")
				.areaCode("32")
				.contentTypeId(TourismType.ACCOMMODATION.getId())
				.serviceKey(DUMMY_SERVICE_KEY)
				.build();

			// When
			var result = sut.fetchPlaceData(req);

			// Then
			assertThat(result).hasSize(3);
			assertThat(result.get(0).getContentId()).isEqualTo(3513119);
			assertThat(result.get(1).getTitle()).isEqualTo("숲속의연인별");
			assertThat(result.get(2).getTitle()).isEqualTo("더앤리조트");
		}

		@Test
		@DisplayName("빈 배열이면 빈 리스트")
		void emptyArray_returnsEmptyList() {
			WebClient client = stubbedWebClientByUrl(Map.of("areaBasedList2", PLACE_LIST_EMPTY), HttpStatus.OK);
			var sut = newSut(client);
			var req = defaultPlaceReq();
			var result = sut.fetchPlaceData(req);
			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("item 노드가 없으면 빈 리스트")
		void noItemNode_returnsEmptyList() {
			WebClient client = stubbedWebClientByUrl(Map.of("areaBasedList2", PLACE_LIST_NO_ITEM), HttpStatus.OK);
			var sut = newSut(client);
			var req = defaultPlaceReq();
			var result = sut.fetchPlaceData(req);
			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("잘못된 JSON이면 런타임 예외")
		void invalidJson_throwsRuntime() {
			WebClient client = stubbedWebClientByUrl(Map.of("areaBasedList2", INVALID_JSON), HttpStatus.OK);
			var sut = newSut(client);
			var req = defaultPlaceReq();

			assertThatThrownBy(() -> sut.fetchPlaceData(req))
				.isInstanceOf(RuntimeException.class)
				.hasMessageContaining("파싱");
		}

		private TourApiPlaceRequestDto defaultPlaceReq() {
			return TourApiPlaceRequestDto.builder()
				.numOfRows(NUM_OF_ROWS)
				.pageNo(PAGE_NO)
				.mobileOS("ETC")
				.mobileApp("tripsok-batch")
				.type("json")
				.arrange("R")
				.areaCode("32")
				.contentTypeId(TourismType.ACCOMMODATION.getId())
				.serviceKey(DUMMY_SERVICE_KEY)
				.build();
		}
	}

	@Nested
	@DisplayName("fetchPlaceDataDetail")
	class FetchPlaceDataDetail {

		@Test
		@DisplayName("item이 있으면 첫 요소 반환")
		void itemExists_returnsFirst() throws Exception {
			WebClient client = stubbedWebClientByUrl(Map.of("detailCommon2", PLACE_DETAIL_OK), HttpStatus.OK);
			var sut = newSut(client);

			var req = TourApiPlaceDetailRequestDto.builder()
				.mobileOS("ETC")
				.mobileApp("tripsok-batch")
				.responseType("json")
				.contentId(333)
				.serviceKey(DUMMY_SERVICE_KEY)
				.build();

			var dto = sut.fetchPlaceDataDetail(req);

			assertThat(dto.getContentId()).isEqualTo("3513119");
			assertThat(dto.getTitle()).isEqualTo("삼박한집");
		}

		@Test
		@DisplayName("item이 없으면 도메인 예외")
		void noItem_throwsNotFound() {
			WebClient client = stubbedWebClientByUrl(Map.of("detailCommon2", PLACE_DETAIL_NO_ITEM), HttpStatus.OK);
			var sut = newSut(client);

			var req = TourApiPlaceDetailRequestDto.builder()
				.mobileOS("ETC")
				.mobileApp("tripsok-batch")
				.responseType("json")
				.contentId(333)
				.serviceKey(DUMMY_SERVICE_KEY)
				.build();

			assertThatThrownBy(() -> sut.fetchPlaceDataDetail(req))
				.isInstanceOf(TourApiException.class)
				.extracting("errorCode")
				.isEqualTo(InternalErrorCode.PLACE_DETAIL_NOT_FOUND);
		}

		@Test
		@DisplayName("잘못된 JSON이면 런타임 예외")
		void invalidJson_throwsRuntime() {
			WebClient client = stubbedWebClientByUrl(Map.of("detailCommon2", INVALID_JSON), HttpStatus.OK);
			var sut = newSut(client);

			var req = TourApiPlaceDetailRequestDto.builder()
				.mobileOS("ETC")
				.mobileApp("tripsok-batch")
				.responseType("json")
				.contentId(333)
				.serviceKey(DUMMY_SERVICE_KEY)
				.build();

			assertThatThrownBy(() -> sut.fetchPlaceDataDetail(req))
				.isInstanceOf(RuntimeException.class)
				.hasMessageContaining("오류가");
		}
	}

	@Nested
	@DisplayName("fetchCategories")
	class FetchCategories {

		@Test
		@DisplayName("정상 응답이면 3레벨 코드로 매핑")
		void itemExists_mapsByThirdCode() {
			WebClient client = stubbedWebClientByUrl(Map.of("lclsSystmCode2", CAT_OK), HttpStatus.OK);
			var sut = newSut(client);

			var req = LclsSystmCodeRequestDto.builder()
				.serviceKey(DUMMY_SERVICE_KEY)
				.pageNo(1)
				.numOfRows(3)
				._type("json")
				.lclsSystm1("")
				.lclsSystm2("")
				.lclsSystm3("")
				.lclsSystmListYn("Y")
				.build();

			Map<String, LclsCategoryItemResponseDto> map = sut.fetchCategories(req);

			assertThat(map).hasSize(3);
			assertThat(map.get("AC010100").getLclsSystm3Nm()).isEqualTo("호텔");
			assertThat(map.get("AC020100").getRnum()).isEqualTo(2);
		}

		@Test
		@DisplayName("item이 없으면 빈 맵")
		void noItem_returnsEmptyMap() {
			WebClient client = stubbedWebClientByUrl(Map.of("lclsSystmCode2", CAT_NO_ITEM), HttpStatus.OK);
			var sut = newSut(client);

			var req = LclsSystmCodeRequestDto.builder()
				.serviceKey(DUMMY_SERVICE_KEY)
				.pageNo(1)
				.numOfRows(3)
				._type("json")
				.lclsSystm1("")
				.lclsSystm2("")
				.lclsSystm3("")
				.lclsSystmListYn("Y")
				.build();

			Map<String, LclsCategoryItemResponseDto> map = sut.fetchCategories(req);
			assertThat(map).isEmpty();
		}

		@Test
		@DisplayName("잘못된 JSON이면 런타임 예외")
		void invalidJson_throwsRuntime() {
			WebClient client = stubbedWebClientByUrl(Map.of("lclsSystmCode2", INVALID_JSON), HttpStatus.OK);
			var sut = newSut(client);

			var req = LclsSystmCodeRequestDto.builder()
				.serviceKey(DUMMY_SERVICE_KEY)
				.pageNo(1)
				.numOfRows(3)
				._type("json")
				.lclsSystm1("")
				.lclsSystm2("")
				.lclsSystm3("")
				.lclsSystmListYn("Y")
				.build();

			assertThatThrownBy(() -> sut.fetchCategories(req))
				.isInstanceOf(RuntimeException.class)
				.hasMessageContaining("파싱");
		}
	}
}
