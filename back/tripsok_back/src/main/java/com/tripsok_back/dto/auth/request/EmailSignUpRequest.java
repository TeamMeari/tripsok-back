package com.tripsok_back.dto.auth.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class EmailSignUpRequest {
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "닉네임은 필수 입력값입니다")
	private String nickname;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "비밀번호는 필수 입력값입니다")
	private String password;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "이메일 인증 후 사용자에게 발급되는 토큰입니다")
	@NotNull(message = "이메일 인증 토큰은 필수 입력값입니다")
	private String emailVerifyToken;
	private String countryCode;
	private List<Integer> interestThemeIds;
}
