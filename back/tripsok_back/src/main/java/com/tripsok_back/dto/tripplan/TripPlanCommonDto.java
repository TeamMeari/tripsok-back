package com.tripsok_back.dto.tripplan;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tripsok_back.model.tripplan.TripPlan;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TripPlanCommonDto {

	@Schema(description = "여행 시작 날짜", example = "2025-12-25")
	private LocalDate tripDate;

	@JsonFormat(pattern = "HH:mm")
	@Schema(description = "여행 시작 시간", example = "09:00")
	private LocalTime startTime;

	@Schema(description = "여행 인원 수", example = "null")
	private Integer numberOfPeople;

	public TripPlanCommonDto(TripPlan tripPlan) {
		this.tripDate = tripPlan.getTripDate();
		this.startTime = tripPlan.getStartTime();
		this.numberOfPeople = tripPlan.getNumberOfPeople();
	}
}
