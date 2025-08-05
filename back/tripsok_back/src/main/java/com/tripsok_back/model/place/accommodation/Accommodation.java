package com.tripsok_back.model.place.accommodation;

import java.time.Instant;

import com.tripsok_back.dto.TourApiPlaceDetailResponseDto;
import com.tripsok_back.dto.TourApiPlaceResponseDto;
import com.tripsok_back.util.TimeUtil;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ACCOMMODATION", schema = "TRIPSOK")
public class Accommodation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Integer id;

	@Size(max = 255)
	@Column(name = "ACCOMMODATION_TYPE")
	private String accommodationType;

	@NotNull
	@Column(name = "CREATED_AT", nullable = false)
	private Instant createdAt;

	public static Accommodation buildAccommodation(TourApiPlaceResponseDto placeDto,
		TourApiPlaceDetailResponseDto detailResponseDto) {
		Accommodation accommodation = new Accommodation();

		accommodation.setAccommodationType(detailResponseDto.getLargeClassificationSystem1());
		accommodation.setCreatedAt(TimeUtil.StringToInstant(placeDto.getCreatedTime()));

		return accommodation;
	}

	public void updateAccommodation(TourApiPlaceResponseDto placeDto, TourApiPlaceDetailResponseDto detailResponseDto) {
		this.accommodationType = detailResponseDto.getLargeClassificationSystem1();
	}
}