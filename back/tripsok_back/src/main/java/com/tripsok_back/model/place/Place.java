package com.tripsok_back.model.place;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "PLACE")
public class Place {
	@Id
	@Size(max = 255)
	@Column(name = "PLACE_ID", nullable = false)
	private String placeId;

	@Size(max = 255)
	@Column(name = "PLACE_NAME")
	private String placeName;

	@Size(max = 255)
	@Column(name = "ADDRESS")
	private String address;

	@Size(max = 255)
	@Column(name = "CONTACT")
	private String contact;

	@Size(max = 255)
	@Column(name = "EMAIL")
	private String email;

	@Lob
	@Column(name = "INFORMATION")
	private String information;

	@Size(max = 255)
	@Column(name = "\"view\"")
	private String view;

	@Size(max = 255)
	@Column(name = "\"like\"")
	private String like;

	@NotNull
	@Column(name = "TOUR_ID", nullable = false)
	private Long tourId;

	@NotNull
	@Column(name = "RESTAURANT_ID", nullable = false)
	private Long restaurantId;

	@NotNull
	@Column(name = "ACCOMODATION_ID", nullable = false)
	private Long accomodationId;

	@Column(name = "CREATED_AT")
	private Instant createdAt;

}