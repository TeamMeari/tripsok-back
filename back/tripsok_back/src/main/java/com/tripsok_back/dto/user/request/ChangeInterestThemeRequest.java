package com.tripsok_back.dto.user.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ChangeInterestThemeRequest{
	@Schema(description = "전체 관심 테마 리스트를 보내주세요!\n빈 리스트를 보내면 관심 테마가 모두 삭제됩니다.")
	@NotNull(message = "관심 테마 리스트는 null이 될 수 없습니다.")
	List<Integer> interestThemeIds;
}
