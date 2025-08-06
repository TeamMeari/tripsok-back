package com.tripsok_back.service;

import static com.tripsok_back.model.user.SocialType.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.tripsok_back.config.OAuth2Properties;
import com.tripsok_back.dto.auth.GoogleUserInfo;
import com.tripsok_back.dto.auth.request.EmailSignUpRequest;
import com.tripsok_back.dto.auth.request.OauthLoginRequest;
import com.tripsok_back.dto.auth.request.OauthSignUpRequest;
import com.tripsok_back.dto.auth.response.GoogleTokenResponse;
import com.tripsok_back.dto.auth.response.TokenResponse;
import com.tripsok_back.exception.AuthException;
import com.tripsok_back.exception.ErrorCode;
import com.tripsok_back.model.Theme.Theme;
import com.tripsok_back.model.auth.RefreshToken;
import com.tripsok_back.model.user.InterestTheme;
import com.tripsok_back.model.user.Role;
import com.tripsok_back.model.user.SocialType;
import com.tripsok_back.model.user.TripSokUser;
import com.tripsok_back.repository.InterestThemeRepository;
import com.tripsok_back.repository.RedisRefreshTokenRepository;
import com.tripsok_back.repository.ThemeRepository;
import com.tripsok_back.repository.UserRepository;
import com.tripsok_back.security.dto.TripSokUserDto;
import com.tripsok_back.security.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
	private final BCryptPasswordEncoder passwordEncoder;
	private final RedisRefreshTokenRepository refreshTokenRepository;
	private final UserRepository userRepository;
	private final OAuth2Properties oAuth2Properties;
	private final JwtUtil jwtUtil;
	private final AuthenticationManager authenticationManager;
	private final InterestThemeService interestThemeService;

	private final RestClient restClient = RestClient.builder().build();

	@Transactional
	public void signUpEmail(EmailSignUpRequest request) {
		String email = jwtUtil.validateAndExtract(request.getEmailVerifyToken(), "email", String.class);
		String password = request.getPassword();
		if (password == null || !validatePassword(password)) {
			throw new AuthException(ErrorCode.INVALID_PASSWORD_FORMAT);
		}
		TripSokUser user = TripSokUser.signUpUser(request.getNickname(), SocialType.EMAIL, null, email,
			passwordEncoder.encode(password), request.getCountryCode(), null);
		validateRegisteredAndSave(user);
		interestThemeService.saveInterestThemes(user, request.getInterestThemeIds());
	}

	@Transactional
	public TokenResponse signUpOAuth(OauthSignUpRequest request) {
		String socialSignUpToken = request.getSocialSignUpToken();
		SocialType type;
		try {
			type = SocialType.valueOf(jwtUtil.validateAndExtract(socialSignUpToken, "socialType", String.class).toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new AuthException(ErrorCode.UNSUPPORTED_SOCIAL_TYPE);
		}
		String socialAccessToken = jwtUtil.validateAndExtract(socialSignUpToken, "authAccessToken", String.class);
		TripSokUser user;
		switch (type) {
			case GOOGLE -> {
				GoogleUserInfo oauthGoogleUserInfo = getGoogleUserInfo(socialAccessToken);
				user = TripSokUser.signUpUser(request.getNickname(), GOOGLE, oauthGoogleUserInfo.getSub(),
					oauthGoogleUserInfo.getEmail(), null, request.getCountryCode(), null);
			}
			default -> throw new AuthException(ErrorCode.UNSUPPORTED_SOCIAL_TYPE);
		}
		validateRegisteredAndSave(user);
		interestThemeService.saveInterestThemes(user, request.getInterestThemeIds());

		return getTokenResponse(user.getId().toString(), getAuthorities(user.getRole()));
	}

	@Transactional
	public TokenResponse loginWithOauth2(OauthLoginRequest request) {
		TripSokUser user;
		switch (request.getSocialType()) {
			case GOOGLE -> {
				GoogleTokenResponse tokenResponse = getGoogleToken(request.getCode());
				GoogleUserInfo googleUserInfo = getGoogleUserInfo(tokenResponse.getIdToken());
				user = userRepository.findBySocialIdAndSocialType(googleUserInfo.getSub(), GOOGLE);
				if (user == null) {
					return new TokenResponse(jwtUtil.generateOAuth2Token(tokenResponse.getIdToken(), GOOGLE), null);
				}
			}
			default -> throw new AuthException(ErrorCode.UNSUPPORTED_SOCIAL_TYPE);
		}
		return getTokenResponse(user.getId().toString(), getAuthorities(user.getRole()));
	}

	@Transactional
	public TokenResponse loginWithEmail(String email, String password) {
		try {
			Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(email, password));
			TripSokUserDto userDetails = (TripSokUserDto)authentication.getPrincipal();

			return getTokenResponse(userDetails.getUserId(), userDetails.getAuthorities());
		} catch (Exception e) {
			log.error("Login failed for email: {}", email, e);
			throw new AuthException(ErrorCode.INVALID_CREDENTIALS);
		}
	}

	@Transactional
	public TokenResponse refresh(String refreshToken) {
		String userId = jwtUtil.validateAndExtract(refreshToken, "userId", String.class);

		String storedToken = refreshTokenRepository.findByUserId(userId).getToken();
		if (!refreshToken.equals(storedToken)) {
			throw new AuthException(ErrorCode.INVALID_REFRESH_TOKEN);
		}

		TripSokUser user = userRepository.findById(Integer.parseInt(userId))
			.orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));

		return getTokenResponse(user.getId().toString(), getAuthorities(user.getRole()));
	}

	public boolean validateNickname(String nickname) {
		return !userRepository.existsByName(nickname);
	}

	private TokenResponse getTokenResponse(String userId, Collection<GrantedAuthority> authorities) {
		String accessToken = jwtUtil.generateAccessToken(userId, authorities);
		String refreshToken = jwtUtil.generateRefreshToken(userId);
		RefreshToken existingRefreshToken = refreshTokenRepository.findByUserId(userId);
		if (existingRefreshToken != null) {
			refreshTokenRepository.delete(existingRefreshToken);
		}
		refreshTokenRepository.save(new RefreshToken(userId, refreshToken));
		return new TokenResponse(accessToken, refreshToken);
	}

	private List<GrantedAuthority> getAuthorities(Role role) {
		return role.getAuthority().stream()
			.map(SimpleGrantedAuthority::new)
			.collect(Collectors.toList());
	}

	private boolean validatePassword(String password) { //TODO: 비밀번호 유효성 검사 필요한가?
		return password.length() >= 8;
	}

	private GoogleTokenResponse getGoogleToken(String code) {
		String url = oAuth2Properties.getGoogle().getTokenUri();
		String clientSecret = oAuth2Properties.getGoogle().getClientSecret();
		String redirectUri = oAuth2Properties.getGoogle().getRedirectUri();
		String clientId = oAuth2Properties.getGoogle().getClientId();
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("code", code);
		params.add("client_id", clientId);
		params.add("client_secret", clientSecret);
		params.add("redirect_uri", redirectUri);
		params.add("grant_type", "authorization_code");

		try {
			return restClient.post()
				.uri(url)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.body(params)
				.retrieve()
				.toEntity(GoogleTokenResponse.class)
				.getBody();
		} catch (HttpClientErrorException e) {
			log.error("Failed to retrieve Google token: {}", e.getMessage());
			throw new AuthException(ErrorCode.INVALID_SOCIAL_CODE);
		}
	}

	private GoogleUserInfo getGoogleUserInfo(String idToken){
			String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;

			try {
				return restClient.get()
					.uri(url)
					.accept(MediaType.APPLICATION_JSON)
					.retrieve()
					.toEntity(GoogleUserInfo.class)
					.getBody();
			} catch (HttpClientErrorException e) {
				log.error("Failed to retrieve Google user info: {}", e.getMessage());
				throw new AuthException(ErrorCode.INVALID_SOCIAL_TOKEN);
			}
		}

	private void validateRegisteredAndSave(TripSokUser user) {
		TripSokUser existingUser = userRepository.findByEmail(user.getEmail());
		if (existingUser != null) {
			if (existingUser.getSocialType() == user.getSocialType()) {
				throw new AuthException(ErrorCode.REGISTERED_ALREADY);
			} else {
				throw new AuthException(ErrorCode.REGISTERED_ANOTHER_SOCIAL);
			}
		}
		if (!validateNickname(user.getName())) {
			throw new AuthException(ErrorCode.CONFLICT_NICKNAME);
		}
		userRepository.save(user);
	}


}
