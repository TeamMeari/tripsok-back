package com.tripsok_back.model.place;

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
@Table(name = "TOUR_REVIEW")
public class TourReview {
	@Id
	@Column(name = "TOUR_REVIEW_ID", nullable = false)
	private Long id;

	@NotNull
	@Column(name = "TOUR_ID", nullable = false)
	private Long tourId;

	@Size(max = 255)
	@NotNull
	@Column(name = "USER_ID", nullable = false)
	private String userId;

	@Size(max = 255)
	@Column(name = "TOUR_REVIEW")
	private String tourReview;

	@Column(name = "CREATED_AT")
	private Instant createdAt;

	@Column(name = "UPDATED_AT")
	private Instant updatedAt;

}