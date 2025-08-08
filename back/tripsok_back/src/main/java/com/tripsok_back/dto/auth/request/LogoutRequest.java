package com.tripsok_back.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LogoutRequest {
	@NotBlank(message = "엑세스 토큰은 필수입니다.")
	private String accessToken;
}
