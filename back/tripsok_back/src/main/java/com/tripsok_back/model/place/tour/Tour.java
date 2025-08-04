package com.tripsok_back.model.place.tour;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "TOUR")
public class Tour {
	@Id
	@Column(name = "TOUR_ID", nullable = false)
	private Long id;

	@Size(max = 255)
	@Column(name = "TOUR_TYPE")
	private String tourType;

	@Column(name = "CREATED_AT")
	private Instant createdAt;

}