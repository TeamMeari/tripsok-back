package com.tripsok_back.dto.auth.request;

import static com.tripsok_back.common.constants.RegexConstants.*;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NicknameDuplicateCheckRequest {
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = NICKNAME_MESSAGE)
	@NotNull(message = "닉네임은 필수 입력값입니다")
	@Pattern(regexp = NICKNAME_REGEX, message = NICKNAME_MESSAGE)
	private String nickname;
}
