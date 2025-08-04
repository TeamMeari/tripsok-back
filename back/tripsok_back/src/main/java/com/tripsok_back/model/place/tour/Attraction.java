package com.tripsok_back.model.place.tour;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ATTRACTION")
public class Attraction {
	@Id
	@Column(name = "ATTRACTION_ID", nullable = false)
	private Long id;

	@NotNull
	@Column(name = "TOUR_ID", nullable = false)
	private Long tourId;

	@Size(max = 255)
	@Column(name = "ATTRACTION_NAME")
	private String attractionName;

	@Size(max = 255)
	@Column(name = "ATTRACTION_PRICE")
	private String attractionPrice;

	@Size(max = 255)
	@Column(name = "ATTRACTION_INFORMATION")
	private String attractionInformation;

	@Column(name = "CREATED_AT")
	private Instant createdAt;

}