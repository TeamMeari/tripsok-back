package com.tripsok_back.service.place;

import static com.tripsok_back.exception.ErrorCode.*;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripsok_back.config.ApiKeyConfig;
import com.tripsok_back.dto.PageResponse;
import com.tripsok_back.dto.place.PlaceBriefSlimResponseDto;
import com.tripsok_back.dto.place.PlaceDetailResponseDto;
import com.tripsok_back.dto.place.ReviewRequestDto;
import com.tripsok_back.exception.PlaceException;
import com.tripsok_back.exception.ServiceBlockException;
import com.tripsok_back.model.place.Place;
import com.tripsok_back.model.place.PlaceIntro;
import com.tripsok_back.dto.tourApi.TourApiIntroRequestDto;
import com.tripsok_back.dto.tourApi.TourApiIntroResponseDto;
import com.tripsok_back.repository.place.PlaceRepository;
import com.tripsok_back.service.search.PlaceEsService;
import com.tripsok_back.type.LocaleCode;
import com.tripsok_back.type.TourismType;
import com.tripsok_back.util.GoogleTranslateClient;
import com.tripsok_back.util.TouristApiClientUtil;
import com.tripsok_back.util.llm.LlmClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class PlaceService {
	final ApiKeyConfig apiKeyConfig;
	final TouristApiClientUtil tourApiClient;
	final CategoryService categoryService;
	final LlmClient groqApiClientUtil;
	final ObjectMapper om;
	final GoogleTranslateClient googleTranslateClient;
	final PlaceEsService placeEsService;
	private final PlaceRepository placeRepository;

	@Transactional
	public void addLike(Place place) {
		place.incrementLikeCount();
	}

	@Transactional
	public void removeLike(Place place) {
		place.decrementLikeCount();
	}

	public Place findPlaceById(int placeId) {
		return placeRepository.findById(placeId).orElseThrow(() -> new PlaceException(PLACE_NOT_FOUND));
	}

	public Set<Place> findPlacesByIds(Set<Integer> placeIds) {
		return placeRepository.findByIdIn(placeIds);
	}

	protected void addView(Place place) {
		place.incrementViewCount();
	}

	public abstract TourismType getType();

	public abstract void startPlaceUpdate(int numOfRow, int pageNo) throws ServiceBlockException;

	public abstract Optional<PlaceDetailResponseDto> getPlaceDetail(int placeId, LocaleCode locale);

	public abstract PageResponse<PlaceBriefSlimResponseDto> getPlaceList(Pageable pageable, LocaleCode locale);

	public abstract PageResponse<PlaceBriefSlimResponseDto> getPlaceListByTheme(Pageable pageable, Integer themeId,
		LocaleCode locale);

	public abstract void addReview(Integer userId, ReviewRequestDto reviewRequestdto);

    public abstract Page<Place> findAll(PageRequest of);

    public abstract int reindexFullEs();

    protected void ensurePlaceIntro(Place place) {
		log.info("ensurePlaceIntro:{} , {}", place.getContentId(), place.getTourismType());
        if (place == null) return;

        boolean missing = place.getPlaceIntro() == null
            || (isBlank(place.getPlaceIntro().getOpenDate())
                && isBlank(place.getPlaceIntro().getRestDate())
                && isBlank(place.getPlaceIntro().getUseTime()));
        if (!missing) return;

        TourApiIntroRequestDto req = TourApiIntroRequestDto.builder()
            .numOfRows(1)
            .pageNo(1)
            .contentId(place.getContentId())
            .contentTypeId(getType().getId())
            .serviceKey(apiKeyConfig.getTourApiKey())
            .build();

        String raw;
        TourApiIntroResponseDto intro = null;
        try {
            raw = tourApiClient.fetchPlaceIntroRaw(req);
        } catch (ServiceBlockException e) {
            return;
        }
        if (isBlank(raw)) return;
        try {
            intro = om.readValue(raw, TourApiIntroResponseDto.class);
        } catch (Exception ignore) { }
        if (intro == null) return;
        PlaceIntro pi = place.getPlaceIntro();
        if (pi == null) {
            pi = new PlaceIntro();
            pi.setPlace(place);
            place.setPlaceIntro(pi);
        }
        if (!isBlank(raw)) {
            pi.setRawJson(raw);
        }

        TourismType type = TourismType.fromId(Integer.parseInt(intro.getContentTypeId()));
        String open = null, rest = null, use = null;
        switch (type) {
            case TOURIST_SPOT:
                open = intro.getOpenDate();
                rest = intro.getRestDate();
                use = intro.getUseTime();
                break;
            case RESTAURANT:
                open = intro.getOpenDateFood();
                rest = intro.getRestDateFood();
                use = intro.getOpenTimeFood();
                break;
            case SHOPPING:
                open = intro.getOpenDateShopping();
                rest = intro.getRestDateShopping();
                use = intro.getOpenTimeShopping();
                break;
            case ACCOMMODATION:
                String in = intro.getCheckInTime();
                String out = intro.getCheckOutTime();
                if (!isBlank(in) || !isBlank(out)) {
                    use = (isBlank(in) ? "" : in) + (isBlank(in) || isBlank(out) ? "" : " ~ ") + (isBlank(out) ? "" : out);
                }
                break;
            case CULTURAL_FACILITY:
                rest = intro.getRestDateCulture();
                use = intro.getUseTimeCulture();
                break;
            case LEISURE_SPORTS:
                open = intro.getOpenPeriodLeports();
                rest = intro.getRestDateLeports();
                use = intro.getUseTimeLeports();
                break;
            case FESTIVAL_EVENT:
                open = intro.getEventStartDate();
                rest = intro.getEventEndDate();
                use = intro.getUseTimeFestival();
                break;
            case TRAVEL_COURSE:
                use = intro.getTakeTime();
                break;
            default:
                open = intro.getOpenDate();
                rest = intro.getRestDate();
                use = intro.getUseTime();
        }

        if (!isBlank(open)) pi.setOpenDate(open);
        if (!isBlank(rest)) pi.setRestDate(rest);
        if (!isBlank(use)) pi.setUseTime(normalizeHours(use));
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String normalizeHours(String s) {
        if (s == null) return null;
        String out = s
            .replaceAll("(?i)<br\s*/?>", " / ")
            .replaceAll("<[^>]+>", " ")
            .replaceAll("\r?\n+", " ")
            .replace('\u200B', ' ')
            .replace('\u200C', ' ')
            .replace('\u200D', ' ')
            .replace('\u2060', ' ')
            .replace('\uFEFF', ' ')
            .replaceAll("\s{2,}", " ")
            .trim();
        if (out.length() > 255) out = out.substring(0, 255);
        return out;
    }
}
