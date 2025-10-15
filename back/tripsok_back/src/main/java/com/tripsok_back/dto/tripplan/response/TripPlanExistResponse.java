package com.tripsok_back.dto.tripplan.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

public record TripPlanExistResponse(@Schema(description = "존재 여부", example = "true", requiredMode = Schema.RequiredMode.REQUIRED) Boolean exists, @Schema(description = "수정된 시간") LocalDateTime updatedAt) {}
