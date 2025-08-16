package com.tripsok_back.dto.auth.request;

import com.tripsok_back.model.user.SocialType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class OauthLoginRequest {
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "리다이렉트 후 전달되는 OAuth 인증 코드입니다")
	@NotNull(message = "소셜 인증 코드는 필수 입력값입니다")
	private String code;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "소셜 플랫폼 타입")
	@NotNull(message = "소셜 플랫폼 타입은 필수 입력값입니다")
	private SocialType socialType;
}
