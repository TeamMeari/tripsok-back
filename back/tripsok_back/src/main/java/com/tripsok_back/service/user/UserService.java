package com.tripsok_back.service.user;

import java.util.Set;

import com.tripsok_back.dto.user.response.UserInfoResponse;

public interface UserService {
	UserInfoResponse getUserInfo(Integer userId);

	void changeContactEmail(Integer userId, String emailVerificationToken);

	void changeInterestThemes(Integer userId, Set<Integer> interestThemeIds);
}
