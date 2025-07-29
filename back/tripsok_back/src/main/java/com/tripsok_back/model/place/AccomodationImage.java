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
@Table(name = "ACCOMODATION_IMAGE")
public class AccomodationImage {
	@Id
	@Column(name = "ACCOMODATION_IMAGE_ID", nullable = false)
	private Long id;

	@NotNull
	@Column(name = "ACCOMODATION_ID", nullable = false)
	private Long accomodationId;

	@NotNull
	@Column(name = "ROOM_ID", nullable = false)
	private Long roomId;

	@Column(name = "CREATED_AT")
	private Instant createdAt;

}