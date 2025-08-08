package com.tripsok_back.dto.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ChangeContactEmailRequest{
	@Schema(description = "변경할 이메일로 인증하여 발급 받은 이메일 인증 토큰")
	@NotBlank(message = "이메일 인증 토큰은 필수입니다.")
	String emailVerifyToken;
}
