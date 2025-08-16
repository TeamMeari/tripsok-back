package com.tripsok_back.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class EmailLoginRequest {
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "이메일은 필수 입력값입니다")
	private String email;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "비밀번호는 필수 입력값입니다")
	private String password;
}
