package com.tripsok_back.service.user;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tripsok_back.dto.SliceResponse;
import com.tripsok_back.dto.user.response.InterestThemeResponse;
import com.tripsok_back.dto.user.response.UserInfoResponse;
import com.tripsok_back.exception.ErrorCode;
import com.tripsok_back.exception.UserException;
import com.tripsok_back.model.user.TripSokUser;
import com.tripsok_back.repository.user.UserRepository;
import com.tripsok_back.security.jwt.JwtUtil;
import com.tripsok_back.type.LocaleCode;
import com.tripsok_back.type.PlaceJoinType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final InterestThemeService interestThemeService;
	private final InterestPlaceService interestPlaceService;
	private final JwtUtil jwtUtil;

	@Override
	@Transactional(readOnly = true)
	public UserInfoResponse getUserInfo(Integer userId) {
		TripSokUser user = findUserById(userId);
		List<InterestThemeResponse> interestThemes = interestThemeService.getInterestThemes(user);
		return new UserInfoResponse(user.getName(), user.getEmail(), user.getContactEmail(), user.getCountryCode(),
			user.getSocialType(), interestThemes);
	}

	@Override
	@Transactional
	public void changeContactEmail(Integer userId, String emailVerificationToken) {
		String contactEmail = jwtUtil.validateAndExtract(emailVerificationToken, "email", String.class);
		TripSokUser user = findUserById(userId);
		user.changeContactEmail(contactEmail);
	}

	@Override
	@Transactional
	public void changeInterestThemes(Integer userId, Set<Integer> interestThemeIds) {
		TripSokUser user = findUserById(userId);
		interestThemeService.updateInterestThemes(user, interestThemeIds);
	}

	@Override
	@Transactional
	public void likePlace(Integer userId, Integer placeId) {
		TripSokUser user = findUserById(userId);
		interestPlaceService.toggleInterestPlaces(user, placeId);
	}

	@Override
	public SliceResponse getUserLikedPlaces(Integer userId, Integer size, Integer lastId, PlaceJoinType type, LocaleCode language) {
		TripSokUser user = findUserById(userId);
		return interestPlaceService.getUserLikedPlaces(user, size, lastId, type, language);
	}

	private TripSokUser findUserById(Integer userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
	}
}
