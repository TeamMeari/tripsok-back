package com.tripsok_back.dto.tripplan.request;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.*;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UpdateVisitSpotRequest (
	@Schema(description = "방문할 장소 ID", example = "1", requiredMode = REQUIRED)
	@NotNull
	Integer placeId,
	@Schema(description = "메모", example = "3시에 도착해서 사진 찍기", requiredMode = REQUIRED)
	String memo,
	@Schema(description = "방문할 순서", example = "1", requiredMode = REQUIRED)
	@NotNull
	Integer order){}
