package com.tripsok_back.dto.auth.request;

import static com.tripsok_back.common.constants.RegexConstants.*;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class EmailSignUpRequest {
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = NICKNAME_MESSAGE, example = "tripsok123")
	@NotNull(message = "닉네임은 필수 입력값입니다")
	@Pattern(regexp = NICKNAME_REGEX, message = NICKNAME_MESSAGE)
	private String nickname;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = PASSWORD_MESSAGE)
	@NotNull(message = "비밀번호는 필수 입력값입니다")
	@Pattern(regexp = PASSWORD_REGEX, message = PASSWORD_MESSAGE)
	private String password;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "이메일 인증 후 사용자에게 발급되는 토큰입니다")
	@NotNull(message = "이메일 인증 토큰은 필수 입력값입니다")
	private String emailVerifyToken;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = NAME_MESSAGE, example = "홍")
	@NotBlank(message = "성(First Name)은 필수 입력값입니다")
	@Pattern(regexp = NAME_REGEX, message = NAME_MESSAGE)
	private String firstName;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = NAME_MESSAGE, example = "길동")
	@NotBlank(message = "이름(Last Name)은 필수 입력값입니다")
	@Pattern(regexp = NAME_REGEX, message = NAME_MESSAGE)
	private String lastName;
}
