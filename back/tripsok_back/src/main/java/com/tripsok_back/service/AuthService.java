package com.tripsok_back.service;

import static com.tripsok_back.model.user.SocialType.*;

import java.util.Collection;
import java.util.Date;
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
import com.tripsok_back.model.auth.BlackListAccessToken;
import com.tripsok_back.model.auth.RefreshToken;
import com.tripsok_back.model.user.Role;
import com.tripsok_back.model.user.SocialType;
import com.tripsok_back.model.user.TripSokUser;
import com.tripsok_back.repository.RedisBlackListAccessTokenRepository;
import com.tripsok_back.repository.RedisRefreshTokenRepository;
import com.tripsok_back.repository.UserRepository;
import com.tripsok_back.security.dto.TripSokUserDto;
import com.tripsok_back.security.jwt.JwtUtil;
import com.tripsok_back.service.user.InterestThemeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
	private final BCryptPasswordEncoder passwordEncoder;
	private final RedisRefreshTokenRepository refreshTokenRepository;
	private final RedisBlackListAccessTokenRepository blackListAccessTokenRepository;
	private final UserRepository userRepository;
	private final OAuth2Properties oAuth2Properties;
	private final JwtUtil jwtUtil;
	private final AuthenticationManager authenticationManager;
	private final InterestThemeService interestThemeService;

	private final RestClient restClient = RestClient.builder().build();

	@Transactional
	public void signUpEmail(EmailSignUpRequest request) {
		String email = getEmailFromToken(request.getEmailVerifyToken());
		String password = request.getPassword();
		TripSokUser user = TripSokUser.signUpUser(request.getNickname(), SocialType.EMAIL, null, email,
			passwordEncoder.encode(password), request.getCountryCode());
		validateRegisteredAndSave(user);
		interestThemeService.saveInterestThemes(user, request.getInterestThemeIds());
	}

	@Transactional
	public TokenResponse signUpOAuth(OauthSignUpRequest request) {
		String socialSignUpToken = request.getSocialSignUpToken();
		String type = jwtUtil.validateAndExtract(socialSignUpToken, "socialType", String.class).toUpperCase();
		String socialAccessToken = jwtUtil.validateAndExtract(socialSignUpToken, "authAccessToken", String.class);
		TripSokUser user;
		try {
			SocialType tokenType = SocialType.valueOf(type);
			switch (tokenType) {
				case GOOGLE -> {
					GoogleUserInfo oauthGoogleUserInfo = getGoogleUserInfo(socialAccessToken);
					user = TripSokUser.signUpUser(request.getNickname(), GOOGLE, oauthGoogleUserInfo.getSub(),
						oauthGoogleUserInfo.getEmail(), null, request.getCountryCode());
				}
				default -> throw new Exception();
			}
		} catch (Exception e) {
			throw new AuthException(ErrorCode.UNSUPPORTED_SOCIAL_TYPE, e.getMessage());
		}

		validateRegisteredAndSave(user);
		interestThemeService.saveInterestThemes(user, request.getInterestThemeIds());

		return getTokenResponse(user.getId(), getAuthorities(user.getRole()));
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
		return getTokenResponse(user.getId(), getAuthorities(user.getRole()));
	}

	@Transactional
	public TokenResponse loginWithEmail(String email, String password) {
		try {
			Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(email, password));
			TripSokUserDto userDetails = (TripSokUserDto)authentication.getPrincipal();

			return getTokenResponse(Integer.parseInt(userDetails.getUserId()), userDetails.getAuthorities());
		} catch (Exception e) {
			throw new AuthException(ErrorCode.INVALID_CREDENTIALS, e.getMessage());
		}
	}

	@Transactional
	public TokenResponse refresh(String refreshToken) {
		Integer userId = jwtUtil.validateAndExtract(refreshToken, "userId", Integer.class);

		String storedToken = refreshTokenRepository.findByUserId(userId).getToken();
		if (!refreshToken.equals(storedToken)) {
			throw new AuthException(ErrorCode.INVALID_REFRESH_TOKEN);
		}

		TripSokUser user = userRepository.findById(userId)
			.orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));

		return getTokenResponse(user.getId(), getAuthorities(user.getRole()));
	}

	public boolean nicknameDuplicateCheck(String nickname) {
		return !userRepository.existsByName(nickname);
	}

	public long getRefreshTokenExpirationTime() {
		return jwtUtil.getRefreshTokenExpirationTime();
	}

	public void logout(String accessToken) {
		Integer userId = jwtUtil.validateAndExtract(accessToken, "userId", Integer.class);
		RefreshToken existingRefreshToken = refreshTokenRepository.findByUserId(userId);
		if (existingRefreshToken != null) {
			refreshTokenRepository.delete(existingRefreshToken); // 리프레시 토큰 삭제
			log.info("리프레시 토큰 삭제: {}", userId);
		}
		try {
			Date expiration = jwtUtil.getTokenExpirationTime(accessToken);
			long ttl = (expiration.getTime() - System.currentTimeMillis()) / 1000; // 초로 변환
			if (ttl > 0) {
				blackListAccessTokenRepository.save(new BlackListAccessToken(accessToken, ttl));
				log.info("액세스 토큰 블랙리스트 추가: {}, TTL: {}초", userId, ttl);
			}
		} catch (Exception e) {
			log.error("액세스 토큰 블랙리스트 추가 실패: {}", e.getMessage());
		}
	}

	@Transactional
	public void resetPassword(String emailVerifyToken, String newPassword) {
		String email = getEmailFromToken(emailVerifyToken);
		TripSokUser user = userRepository.findByEmailAndSocialType(email, SocialType.EMAIL)
			.orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));
		user.changePassword(passwordEncoder.encode(newPassword));
	}

	private TokenResponse getTokenResponse(Integer userId, Collection<GrantedAuthority> authorities) {
		String accessToken = jwtUtil.generateAccessToken(userId, authorities);
		String refreshToken = jwtUtil.generateRefreshToken(userId);
		RefreshToken existingRefreshToken = refreshTokenRepository.findByUserId(userId);
		if (existingRefreshToken != null) {
			refreshTokenRepository.delete(existingRefreshToken);
		}
		refreshTokenRepository.save(new RefreshToken(userId, refreshToken, jwtUtil.getRefreshTokenExpirationTime()));
		return new TokenResponse(accessToken, refreshToken);
	}

	private List<GrantedAuthority> getAuthorities(Role role) {
		return role.getAuthority().stream()
			.map(SimpleGrantedAuthority::new)
			.collect(Collectors.toList());
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
			throw new AuthException(ErrorCode.INVALID_SOCIAL_CODE, e.getMessage());
		}
	}

	private GoogleUserInfo getGoogleUserInfo(String idToken) {
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
			throw new AuthException(ErrorCode.INVALID_SOCIAL_SIGNUP_TOKEN);
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
		if (!nicknameDuplicateCheck(user.getName())) {
			throw new AuthException(ErrorCode.CONFLICT_NICKNAME);
		}
		userRepository.save(user);
	}

	private String getEmailFromToken(String token) {
		return jwtUtil.validateAndExtract(token, "email", String.class);
	}
}
