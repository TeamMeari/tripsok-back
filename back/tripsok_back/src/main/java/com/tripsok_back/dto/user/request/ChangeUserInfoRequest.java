package com.tripsok_back.dto.user.request;

import static com.tripsok_back.common.constants.RegexConstants.*;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class ChangeUserInfoRequest {
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = NAME_MESSAGE, example = "길동")
	@NotBlank(message = "이름(first Name)은 필수 입력값입니다")
	@Pattern(regexp = NAME_REGEX, message = NAME_MESSAGE)
	private String firstName;
	@Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = NAME_MESSAGE, example = "홍")
	@NotBlank(message = "성(last Name)은 필수 입력값입니다")
	@Pattern(regexp = NAME_REGEX, message = NAME_MESSAGE)
	private String lastName;
}
