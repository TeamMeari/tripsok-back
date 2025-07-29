package com.tripsok_back.model.place;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "TOUR_IMAGE")
public class TourImage {
	@Id
	@Column(name = "TOUR_IMAGE_ID", nullable = false)
	private Long id;

	@NotNull
	@Column(name = "TOUR_ID", nullable = false)
	private Long tourId;

	@NotNull
	@Column(name = "ATTRACTION_ID", nullable = false)
	private Long attractionId;

	@Column(name = "CREATED_AT")
	private Instant createdAt;

}