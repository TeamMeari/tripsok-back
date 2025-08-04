package com.tripsok_back.model.place.accomodation;

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
@Table(name = "ROOM")
public class Room {
	@Id
	@Column(name = "ROOM_ID", nullable = false)
	private Long id;

	@NotNull
	@Column(name = "ACCOMODATION_ID", nullable = false)
	private Long accomodationId;

	@Size(max = 255)
	@Column(name = "ROOM_NAME")
	private String roomName;

	@Size(max = 255)
	@Column(name = "SEASON_TYPE")
	private String seasonType;

	@Size(max = 255)
	@Column(name = "ROOM_PRICE")
	private String roomPrice;

	@Size(max = 255)
	@Column(name = "ROOM_INFORMATION")
	private String roomInformation;

	@Column(name = "CREATED_AT")
	private Instant createdAt;

}