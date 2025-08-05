package com.tripsok_back.model.place.accommodation;

import java.time.Instant;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ROOM", schema = "TRIPSOK")
public class Room {
	@Id
	@Column(name = "ID", nullable = false)
	private Integer id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@OnDelete(action = OnDeleteAction.RESTRICT)
	@JoinColumn(name = "ACCOMMODATION_ID", nullable = false)
	private Accommodation accommodation;

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

	@NotNull
	@Column(name = "CREATED_AT", nullable = false)
	private Instant createdAt;

}