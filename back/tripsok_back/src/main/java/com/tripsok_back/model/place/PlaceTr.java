package com.tripsok_back.model.place;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "PLACE_TR")
public class PlaceTr {
	@EmbeddedId
	private PlaceTrId id;

	@MapsId("placeId")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "PLACE_ID", nullable = false)
	private Place place;

	@Size(max = 255)
	@Column(name = "PLACE_NAME")
	private String placeName;

	@Size(max = 255)
	@Column(name = "ADDRESS")
	private String address;

	@Lob
	@Column(name = "INFORMATION")
	private String information;

	@Size(max = 255)
	@Column(name = "SUMMARY")
	private String summary;

}