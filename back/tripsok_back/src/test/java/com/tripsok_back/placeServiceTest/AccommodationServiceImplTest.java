package com.tripsok_back.placeServiceTest;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripsok_back.config.ApiKeyConfig;
import com.tripsok_back.dto.place.PlaceDetailResponseDto;
import com.tripsok_back.dto.tourApi.TourApiPlaceDetailRequestDto;
import com.tripsok_back.dto.tourApi.TourApiPlaceDetailResponseDto;
import com.tripsok_back.dto.tourApi.TourApiPlaceRequestDto;
import com.tripsok_back.dto.tourApi.TourApiPlaceResponseDto;
import com.tripsok_back.exception.TourApiException;
import com.tripsok_back.model.place.Place;
import com.tripsok_back.repository.place.accomodation.AccommodationRepository;
import com.tripsok_back.service.place.AccommodationServiceImpl;
import com.tripsok_back.service.place.CategoryService;
import com.tripsok_back.type.PlaceJoinType;
import com.tripsok_back.type.TourismType;
import com.tripsok_back.util.TimeUtil;
import com.tripsok_back.util.TouristApiClientUtil;

@ExtendWith(MockitoExtension.class)
class AccommodationServiceImplTest {

	@Mock
	ApiKeyConfig apiKeyConfig;
	@Mock
	TouristApiClientUtil tourApiClient;
	@Mock
	CategoryService categoryService;
	@Mock
	AccommodationRepository accommodationRepository;
	@Spy
	ObjectMapper om = new ObjectMapper();

	@Spy
	@InjectMocks
	AccommodationServiceImpl sut;

	@Captor
	ArgumentCaptor<TourApiPlaceRequestDto> placeReqCaptor;
	@Captor
	ArgumentCaptor<TourApiPlaceDetailRequestDto> detailReqCaptor;

	private static final String CATEGORY_NAME = TourismType.ACCOMMODATION.name();

	private void stubApiKey() {
		when(apiKeyConfig.getTourApiKey()).thenReturn("test-tour-api-key");
	}

	@Test
	@DisplayName("getType: ACCOMMODATION 반환")
	void getType_returnsAccommodation() {
		assertThat(sut.getType()).isEqualTo(TourismType.ACCOMMODATION);
	}

	// ===== requestPlace / requestPlaceDetail =====

	@Nested
	@DisplayName("requestPlace")
	class RequestPlace {

		@Test
		@DisplayName("DTO 올바르게 구성되고 client로 위임")
		void buildsDto_andDelegates() {
			stubApiKey();
			when(tourApiClient.fetchPlaceData(any()))
				.thenReturn(
					List.of(TourApiPlaceResponseDto.builder().contentId(111).modifiedTime("20991231235959").build()));

			List<TourApiPlaceResponseDto> res = sut.requestPlace(50, 2);

			verify(tourApiClient).fetchPlaceData(placeReqCaptor.capture());
			TourApiPlaceRequestDto sent = placeReqCaptor.getValue();
			assertThat(res).hasSize(1);
			assertThat(sent.getNumOfRows()).isEqualTo(50);
			assertThat(sent.getPageNo()).isEqualTo(2);
			assertThat(sent.getMobileOS()).isEqualTo("ETC");
			assertThat(sent.getMobileApp()).isEqualTo("tripsok-batch");
			assertThat(sent.getType()).isEqualTo("json");
			assertThat(sent.getArrange()).isEqualTo("R");
			assertThat(sent.getAreaCode()).isEqualTo("32");
			assertThat(sent.getContentTypeId()).isEqualTo(TourismType.ACCOMMODATION.getId());
			assertThat(sent.getServiceKey()).isEqualTo("test-tour-api-key");
		}
	}

	@Nested
	@DisplayName("requestPlaceDetail")
	class RequestPlaceDetail {

		@Test
		@DisplayName("DTO 올바르게 구성되고 client로 위임")
		void buildsDto_andDelegates() {
			stubApiKey();
			TourApiPlaceDetailResponseDto dto = mock(TourApiPlaceDetailResponseDto.class);
			when(tourApiClient.fetchPlaceDataDetail(any())).thenReturn(dto);

			TourApiPlaceDetailResponseDto out = sut.requestPlaceDetail(9999);

			verify(tourApiClient).fetchPlaceDataDetail(detailReqCaptor.capture());
			TourApiPlaceDetailRequestDto sent = detailReqCaptor.getValue();
			assertThat(out).isSameAs(dto);
			assertThat(sent.getMobileOS()).isEqualTo("ETC");
			assertThat(sent.getMobileApp()).isEqualTo("tripsok-batch");
			assertThat(sent.getResponseType()).isEqualTo("json");
			assertThat(sent.getContentId()).isEqualTo(9999);
			assertThat(sent.getServiceKey()).isEqualTo("test-tour-api-key");
		}
	}

	// ===== checkAndUpdatePlace 분기 =====

	@Nested
	@DisplayName("checkAndUpdatePlace")
	class CheckAndUpdatePlace {

		@Test
		@DisplayName("신규: findByContentId 없음 → addPlace → save 호출, true")
		void new_adds_andSaves_returnsTrue() {
			stubApiKey();
			TourApiPlaceResponseDto incoming = TourApiPlaceResponseDto.builder()
				.contentId(1001).modifiedTime("20991231235959").build();

			when(accommodationRepository.findByContentId(1001)).thenReturn(Optional.empty());

			TourApiPlaceDetailResponseDto detail = mock(TourApiPlaceDetailResponseDto.class);
			when(tourApiClient.fetchPlaceDataDetail(any())).thenReturn(detail);

			Place built = mock(Place.class);
			try (MockedStatic<Place> placeStatic = mockStatic(Place.class)) {
				placeStatic.when(() -> Place.buildAccommodation(incoming, detail, CATEGORY_NAME)).thenReturn(built);

				Boolean updated = sut.checkAndUpdatePlace(incoming);

				assertThat(updated).isTrue();
				verify(accommodationRepository).save(built);
			}
		}

		@Test
		@DisplayName("변경 없음: updatedAt 동일 → false, save/상세호출 없음")
		void unchanged_returnsFalse_noSave() {
			String modified = "20250101010101";
			TourApiPlaceResponseDto incoming = TourApiPlaceResponseDto.builder()
				.contentId(2001).modifiedTime(modified).build();

			LocalDateTime parsed = TimeUtil.stringToLocalDateTime(modified);

			Place existing = mock(Place.class);
			when(existing.getUpdatedAt()).thenReturn(parsed);
			when(accommodationRepository.findByContentId(2001)).thenReturn(Optional.of(existing));

			Boolean updated = sut.checkAndUpdatePlace(incoming);

			assertThat(updated).isFalse();
			verify(accommodationRepository, never()).save(any());
			verifyNoInteractions(tourApiClient);
		}

		@Test
		@DisplayName("변경 있음: updatedAt 다름 → updatePlace 경로, save 호출, true")
		void changed_updates_andSaves_returnsTrue() {
			stubApiKey();
			TourApiPlaceResponseDto incoming = TourApiPlaceResponseDto.builder()
				.contentId(3001).modifiedTime("20991231235959").build();

			Place existing = mock(Place.class);
			when(existing.getContentId()).thenReturn(3001);
			when(existing.getUpdatedAt()).thenReturn(LocalDateTime.of(2000, 1, 1, 0, 0));

			when(accommodationRepository.findByContentId(3001)).thenReturn(Optional.of(existing));

			TourApiPlaceDetailResponseDto detail = mock(TourApiPlaceDetailResponseDto.class);
			when(detail.getCategoryLevel3()).thenReturn("C03");
			when(detail.getLargeClassificationSystem3()).thenReturn("C03");
			when(tourApiClient.fetchPlaceDataDetail(any())).thenReturn(detail);
			when(categoryService.getCategoryByCode("C03")).thenReturn("호텔");

			Boolean updated = sut.checkAndUpdatePlace(incoming);

			assertThat(updated).isTrue();
			verify(existing).updateAccommodation(eq(incoming), eq(detail), eq("호텔"));
			verify(accommodationRepository).save(existing);
		}
	}

	// ===== updatePlace / addPlace =====

	@Nested
	@DisplayName("updatePlace")
	class UpdatePlace {

		@Test
		@DisplayName("상세 호출 → 카테고리 매핑 → 엔티티 업데이트 → save")
		void updates_withDetail_andSaves() {
			stubApiKey();
			Place existing = mock(Place.class);
			when(existing.getContentId()).thenReturn(777);

			TourApiPlaceResponseDto incoming = TourApiPlaceResponseDto.builder()
				.contentId(777).modifiedTime("20991231235959").build();

			TourApiPlaceDetailResponseDto detail = mock(TourApiPlaceDetailResponseDto.class);
			when(detail.getLargeClassificationSystem3()).thenReturn("C03");
			when(tourApiClient.fetchPlaceDataDetail(any())).thenReturn(detail);
			when(categoryService.getCategoryByCode("C03")).thenReturn("호텔");

			sut.updatePlace(existing, incoming);

			verify(existing).updateAccommodation(incoming, detail, "호텔");
			verify(accommodationRepository).save(existing);
		}
	}

	@Nested
	@DisplayName("addPlace")
	class AddPlace {

		@Test
		@DisplayName("상세 호출 → Place.buildAccommodation → save")
		void builds_andSaves() {
			stubApiKey();
			TourApiPlaceResponseDto incoming = TourApiPlaceResponseDto.builder()
				.contentId(888).modifiedTime("20991231235959").build();

			TourApiPlaceDetailResponseDto detail = mock(TourApiPlaceDetailResponseDto.class);
			when(tourApiClient.fetchPlaceDataDetail(any())).thenReturn(detail);

			Place built = mock(Place.class);
			try (MockedStatic<Place> placeStatic = mockStatic(Place.class)) {
				placeStatic.when(() -> Place.buildAccommodation(incoming, detail, CATEGORY_NAME)).thenReturn(built);

				sut.addPlace(incoming);

				verify(accommodationRepository).save(built);
			}
		}
	}

	// ===== getPlaceDetail 경로 =====

	@Nested
	@DisplayName("getPlaceDetail")
	class GetPlaceDetail {

		@Test
		@DisplayName("미존재 → TourApiException")
		void notFound_throws() {
			when(accommodationRepository.findById(999)).thenReturn(Optional.empty());
			assertThatThrownBy(() -> sut.getPlaceDetail(999)).isInstanceOf(TourApiException.class);
		}

		@Test
		@DisplayName("존재 + accommodationType 비어있음 → 상세 호출/보강, view 증가, DTO 반환")
		void exist_andTypeEmpty_fetchesDetail_updates_andIncrementsView() {
			stubApiKey();

			Place place = mock(Place.class, RETURNS_DEEP_STUBS);
			when(place.getAccommodation().getAccommodationType()).thenReturn("");
			when(place.getContentId()).thenReturn(3513119);
			when(accommodationRepository.findById(1)).thenReturn(Optional.of(place));

			TourApiPlaceDetailResponseDto detail = mock(TourApiPlaceDetailResponseDto.class);
			when(detail.getCategoryLevel3()).thenReturn("X01");
			when(tourApiClient.fetchPlaceDataDetail(any())).thenReturn(detail);
			when(categoryService.getCategoryByCode("X01")).thenReturn("펜션");

			PlaceDetailResponseDto fake = mock(PlaceDetailResponseDto.class);
			try (MockedStatic<PlaceDetailResponseDto> st = mockStatic(PlaceDetailResponseDto.class)) {
				st.when(() -> PlaceDetailResponseDto.from(eq(place), eq(PlaceJoinType.ACCOMMODATION))).thenReturn(fake);

				PlaceDetailResponseDto out = sut.getPlaceDetail(1).get();

				assertThat(out).isSameAs(fake);
				verify(place).updateNullAccommodationDetail(detail, "펜션");
				verify(place).incrementViewCount();
			}
		}

		@Test
		@DisplayName("존재 + accommodationType 있음 → 상세 호출 안함, view 증가만")
		void exist_andTypePresent_skipsDetail_callsViewOnly() {
			Place place = mock(Place.class, RETURNS_DEEP_STUBS);
			when(place.getAccommodation().getAccommodationType()).thenReturn("호텔");
			when(accommodationRepository.findById(2)).thenReturn(Optional.of(place));

			PlaceDetailResponseDto fake = mock(PlaceDetailResponseDto.class);
			try (MockedStatic<PlaceDetailResponseDto> st = mockStatic(PlaceDetailResponseDto.class)) {
				st.when(() -> PlaceDetailResponseDto.from(eq(place), eq(PlaceJoinType.ACCOMMODATION))).thenReturn(fake);

				PlaceDetailResponseDto out = sut.getPlaceDetail(2).get();

				assertThat(out).isSameAs(fake);
				verifyNoInteractions(tourApiClient);
				verify(place).incrementViewCount();
			}
		}
	}

	@Test
	@DisplayName("addView: incrementViewCount 호출")
	void addView_increments() {
		Place p = mock(Place.class);
		sut.addView(p);
		verify(p).incrementViewCount();
	}

	@Test
	@DisplayName("addLike: incrementLikeCount 호출")
	void addLike_increments() {
		Place p = mock(Place.class);
		sut.addLike(p);
		verify(p).incrementLikeCount();
	}
}
