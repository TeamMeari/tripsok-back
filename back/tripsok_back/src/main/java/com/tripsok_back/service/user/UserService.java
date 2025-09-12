package com.tripsok_back.service.user;

import java.util.Set;

import com.tripsok_back.dto.SliceResponse;
import com.tripsok_back.dto.user.response.UserInfoResponse;
import com.tripsok_back.type.LocaleCode;
import com.tripsok_back.type.PlaceJoinType;

public interface UserService {
	UserInfoResponse getUserInfo(Integer userId);

	void changeContactEmail(Integer userId, String emailVerificationToken);

	void changeInterestThemes(Integer userId, Set<Integer> interestThemeIds);

	void likePlace(Integer userId, Integer placeId);

	SliceResponse getUserLikedPlaces(Integer userId, Integer size, Integer lastId, PlaceJoinType type,
		LocaleCode locale);
}
