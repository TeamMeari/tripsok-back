package com.tripsok_back.dto.auth.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class OauthSignUpRequest {
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "닉네임은 필수 입력값입니다")
	private String nickname;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "소셜 로그인시 회원가입 되어있지 않으면 발급되는 소셜 인증 토큰입니다")
	@NotNull(message = "소셜 인증 토큰은 필수 입력값입니다")
	private String socialSignUpToken;
	private String countryCode;
	private List<Integer> interestThemeIds;
}
