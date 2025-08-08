package com.tripsok_back.dto.user.response;

import java.util.List;

import com.tripsok_back.model.user.SocialType;

public record UserInfoResponse(
	String nickname,
	String email,
	String contactEmail,
	String contryCode,
	SocialType socialType,
	List<InterestThemeResponse> interestThemes
) {
}
