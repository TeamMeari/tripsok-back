package com.tripsok_back.service.auth;

import com.tripsok_back.dto.auth.request.EmailSignUpRequest;
import com.tripsok_back.dto.auth.request.OauthLoginRequest;
import com.tripsok_back.dto.auth.request.OauthSignUpRequest;
import com.tripsok_back.dto.auth.response.ResetPasswordResponse;
import com.tripsok_back.dto.auth.response.TokenResponse;

public interface AuthService {
	void signUpEmail(EmailSignUpRequest request);

	TokenResponse signUpOAuth(OauthSignUpRequest request);

	TokenResponse loginWithOauth2(OauthLoginRequest request);

	TokenResponse loginWithEmail(String email, String password);

	TokenResponse refresh(String refreshToken);

	boolean nicknameDuplicateCheck(String nickname);

	long getRefreshTokenExpirationTime();

	void logout(String accessToken);

	ResetPasswordResponse resetPassword(String emailVerifyToken, String newPassword);

	String getUserAccessToken(Integer userId);
}
