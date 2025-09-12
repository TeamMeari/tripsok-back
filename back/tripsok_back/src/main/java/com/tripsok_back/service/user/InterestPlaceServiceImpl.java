package com.tripsok_back.service.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.tripsok_back.dto.SliceResponse;
import com.tripsok_back.dto.user.response.InterestPlaceResponse;
import com.tripsok_back.model.place.Place;
import com.tripsok_back.model.user.InterestPlace;
import com.tripsok_back.model.user.TripSokUser;
import com.tripsok_back.repository.user.InterestPlaceRepository;
import com.tripsok_back.service.place.PlaceService;
import com.tripsok_back.type.LocaleCode;
import com.tripsok_back.type.PlaceJoinType;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InterestPlaceServiceImpl implements InterestPlaceService {
	private final InterestPlaceRepository interestPlaceRepository;
	private final PlaceService placeService;

	public InterestPlaceServiceImpl(InterestPlaceRepository interestPlaceRepository,
		@Qualifier("placeDefaultServiceImpl") PlaceService placeService) {
		this.interestPlaceRepository = interestPlaceRepository;
		this.placeService = placeService;
	}

	@Override
	public void toggleInterestPlaces(TripSokUser user, Integer placeId) {
		Place place = placeService.findPlaceById(placeId);
		InterestPlace interestPlace = interestPlaceRepository.findInterestPlaceByUserAndPlace(user, place);
		if (interestPlace == null) {
			interestPlaceRepository.save(new InterestPlace(user, place));
			placeService.addLike(place);
		} else {
			interestPlaceRepository.delete(interestPlace);
			placeService.removeLike(place);
		}
	}

	@Override
	public SliceResponse getUserLikedPlaces(TripSokUser user, Integer size, Integer lastId, PlaceJoinType type,
		LocaleCode locale) {
		Pageable pageable = PageRequest.ofSize(size);
		Slice<InterestPlace> interestPlaces = interestPlaceRepository.findInterestPlacesByUser(user, pageable, lastId,
			type.name());

		List<InterestPlaceResponse> interestPlaceResponses = interestPlaces.stream()
			.map(ip -> {
				Place place = ip.getPlace();
				return InterestPlaceResponse.builder()
					.id(ip.getId())
					.placeId(place.getId())
					.language(locale)
					.name(place.getPlaceTr(locale).getPlaceName())
					.type(type)
					.thumbnailUrl(getThumbnailUrl(place, type)).build();
			}).toList();
		return new SliceResponse(interestPlaces.hasNext(), interestPlaceResponses);
	}

	private String getThumbnailUrl(Place place, PlaceJoinType type) {
		return switch (type) {
			case ACCOMMODATION -> place.getAccommodation().getImageUrlList().getFirst();
			case RESTAURANT -> place.getRestaurant().getImageUrlList().getFirst();
			case TOUR -> place.getTour().getImageUrlList().getFirst();
		};
	}
}
