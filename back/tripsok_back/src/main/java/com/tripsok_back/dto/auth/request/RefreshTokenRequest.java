package com.tripsok_back.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class RefreshTokenRequest {
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "리프레시 토큰")
	@NotNull(message = "리프레시 토큰은 필수 입력값입니다")
	private String refreshToken;
}
